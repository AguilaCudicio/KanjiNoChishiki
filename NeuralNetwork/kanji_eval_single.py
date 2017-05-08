# Evaluation for KANJI.
# ==============================================================================

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

from datetime import datetime
import math
import time

import numpy as np
import tensorflow as tf
from PIL import Image

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
	  
	# Start populating the filename queue.
	#TODO: Esto es necesario??
    coord = tf.train.Coordinator()
    threads = tf.train.start_queue_runners(coord=coord)

    top_indices = sess.run([top_k_op])
	
    coord.request_stop()
    coord.join(threads)
  
    print ("Predicted ", top_indices[0], " for your input image.")



def evaluate():
  """Eval KANJI for a single example"""
  with tf.Graph().as_default() as g:
    # Get images and labels for KANJI.
    eval_data = FLAGS.eval_data == 'test'

    images, labels = kanji.single_input(eval_data=eval_data)
        
    # Build a Graph that computes the logits predictions from the
    # inference model.
    logits = kanji.inference(images)

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


def main(argv=None):  # pylint: disable=unused-argument
  evaluate()


if __name__ == '__main__':
  tf.app.run()
