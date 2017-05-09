import os.path
import sys

import tensorflow as tf
from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib

FLAGS = tf.app.flags.FLAGS

tf.app.flags.DEFINE_string('checkpoint_dir', '/tmp/kanji_train',
                           """Directory where to read model checkpoints.""")

# Freeze the graph

input_graph_path =  os.path.join(FLAGS.checkpoint_dir, 'model.pbtxt')
checkpoint_path = os.path.join(FLAGS.checkpoint_dir, 'model.ckpt')
input_saver_def_path = ""
input_binary = False
output_node_names = "softmax_linear"
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
with tf.gfile.Open(output_frozen_graph_name, "r") as f:
    data = f.read()
    input_graph_def.ParseFromString(data)

output_graph_def = optimize_for_inference_lib.optimize_for_inference(
        input_graph_def,
        ["I"], # an array of the input node(s)
        ["softmax_linear"], # an array of output nodes
        tf.float32.as_datatype_enum)

# Save the optimized graph

f = tf.gfile.FastGFile(output_optimized_graph_name, "w")
f.write(output_graph_def.SerializeToString())