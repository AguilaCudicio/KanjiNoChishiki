# Generate an optimized and trained graph file in order to import it to android
# ==============================================================================

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import os.path
import sys


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


def generateFile():
	# Restore the moving average version of the learned variables for eval.
	variable_averages = tf.train.ExponentialMovingAverage(
        kanji.MOVING_AVERAGE_DECAY)
	variables_to_restore = variable_averages.variables_to_restore()
	saver = tf.train.Saver(variables_to_restore)
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
	# Freeze the graph
	input_graph_path =  os.path.join(FLAGS.checkpoint_dir, 'model.pbtxt')
	checkpoint_path = os.path.join(FLAGS.checkpoint_dir, 'model.ckpt-'+global_step)
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


def generateGraph():
  """Generate the graph and its graphDef, freeze it, optimize it and then save it"""
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
 
    generateFile()


def main(argv=None):  # pylint: disable=unused-argument
  generateGraph()


if __name__ == '__main__':
  tf.app.run()
