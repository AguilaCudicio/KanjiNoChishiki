# A binary to train using a single GPU.
# ==============================================================================


from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

from datetime import datetime
import os.path
import time

import numpy as np
from six.moves import xrange  # pylint: disable=redefined-builtin
import tensorflow as tf

import kanji

FLAGS = tf.app.flags.FLAGS

tf.app.flags.DEFINE_string('train_dir', '/tmp/kanji_train',
                           """Directory where to write event logs """
                           """and checkpoint.""")

def listvar():
	gf = tf.GraphDef()
	gf.ParseFromString(open(FLAGS.train_dir+"/optimizedmodel.pb",'rb').read())
	for node in gf.node:
		print (node.name)


def main(argv=None):
  listvar()


if __name__ == '__main__':
  tf.app.run()
