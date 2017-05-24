# Evaluation for KANJI.
# ==============================================================================

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import os.path
import sys


from datetime import datetime
import math
import time

import numpy as np
import tensorflow as tf
from PIL import Image

from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib

import kanji

FLAGS = tf.app.flags.FLAGS

tf.app.flags.DEFINE_string('eval_dir', '/tmp/kanji_eval',
                           """Directory where to write event logs.""")
tf.app.flags.DEFINE_string('eval_data', 'test',
                           """Either 'test' or 'train_eval'.""")
tf.app.flags.DEFINE_string('checkpoint_dir', '/tmp/kanji_train',
                           """Directory where to read model checkpoints.""")
tf.app.flags.DEFINE_integer('eval_interval_secs', 60 * 5,
                            """How often to run the eval.""")
tf.app.flags.DEFINE_integer('num_examples', 10000,
                            """Number of examples to run.""")
tf.app.flags.DEFINE_boolean('run_once', True,
                         """Whether to run eval only once.""")

def eval_once(saver, top_k_op):
  """Run Eval once.

  Args:
    saver: Saver.
    top_k_op: Top K op.
  """
  with tf.Session() as sess:
    ckpt = tf.train.get_checkpoint_state(FLAGS.checkpoint_dir)
    if ckpt and ckpt.model_checkpoint_path:
      # Restores from checkpoint
      saver.restore(sess, ckpt.model_checkpoint_path)
      # Assuming model_checkpoint_path looks something like:
      #   /my-favorite-path/kanji_train/model.ckpt-0,
      # extract global_step from it.
      global_step = ckpt.model_checkpoint_path.split('/')[-1].split('-')[-1]
    else:
      print('No checkpoint file found')
      return
    tf.train.write_graph(sess.graph_def, FLAGS.checkpoint_dir, 'model.pbtxt')

    # Start populating the filename queue.
	#TODO: Esto es necesario??
    coord = tf.train.Coordinator()
    threads = tf.train.start_queue_runners(coord=coord)

    top_indices = sess.run([top_k_op])
	
    coord.request_stop()
    coord.join(threads)

    print ("Predicted ", top_indices[0], " for your input image.")

def generateFile():
	# Freeze the graph
	input_graph_path =  os.path.join(FLAGS.checkpoint_dir, 'model.pbtxt')
	# Remember to change this number according to the highest step.
	checkpoint_path = os.path.join(FLAGS.checkpoint_dir, 'model.ckpt-80000')
	input_saver_def_path = ""
	input_binary = False
	output_node_names = "finalresult"
	restore_op_name = "save/restore_all"
	filename_tensor_name = "save/Const:0"
	output_frozen_graph_name = os.path.join(FLAGS.checkpoint_dir, 'frozenmodel.pb')
	output_optimized_graph_name = os.path.join(FLAGS.checkpoint_dir, 'optimizedmodel.pb')
	clear_devices = True


	freeze_graph.freeze_graph(input_graph_path, input_saver_def_path,
							  input_binary, checkpoint_path, output_node_names,
							  restore_op_name, filename_tensor_name,
							  output_frozen_graph_name, clear_devices, "")



	# Optimize for inference

	input_graph_def = tf.GraphDef()
	with tf.gfile.Open(output_frozen_graph_name, "rb") as f:
		data = f.read()
		input_graph_def.ParseFromString(data)

	output_graph_def = optimize_for_inference_lib.optimize_for_inference(
			input_graph_def,
			["InputI"], # an array of the input node(s)
			["finalresult"], # an array of output nodes
			tf.float32.as_datatype_enum)

	# Save the optimized graph

	f = tf.gfile.FastGFile(output_optimized_graph_name, "w")
	f.write(output_graph_def.SerializeToString())


def evaluate():
  """Eval KANJI for a single example"""
  with tf.Graph().as_default() as g:
    # Get images and labels for KANJI.
    eval_data = FLAGS.eval_data == 'test'

    images, labels = kanji.single_input(eval_data=eval_data)
    images2 = tf.reshape(images,[32,32,3])
    images2 = tf.identity(images2, name='InputI') 
    images2 = tf.image.per_image_standardization(images2)
    images2 = tf.reshape(images2,[1,32,32,3])
    
    # Build a Graph that computes the logits predictions from the
    # inference model.
    logits = kanji.inference(images2)
    final_tensor = tf.nn.softmax(logits, name="finalresult")

    # Calculate predictions.
    top_k_op = tf.nn.top_k(logits, k=5)

    # Restore the moving average version of the learned variables for eval.
    variable_averages = tf.train.ExponentialMovingAverage(
        kanji.MOVING_AVERAGE_DECAY)
    variables_to_restore = variable_averages.variables_to_restore()
    saver = tf.train.Saver(variables_to_restore)


    while True:
      eval_once(saver, top_k_op)
      if FLAGS.run_once:
        break
      time.sleep(FLAGS.eval_interval_secs)
 
    final_tensor = tf.nn.softmax(logits, name="finalresult")
    generateFile()


def main(argv=None):  # pylint: disable=unused-argument
  evaluate()


if __name__ == '__main__':
  tf.app.run()
