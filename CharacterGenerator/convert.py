from PIL import Image
import numpy as np

import os
import glob

filestraining = glob.glob(r'D:\REDES_NEURONALES\generador-caracteres\filea\*')
fileseval = glob.glob(r'D:\REDES_NEURONALES\generador-caracteres\fileb\*')
filesingle = glob.glob(r'D:\REDES_NEURONALES\generador-caracteres\fileb\000000.jpg')


def generate_array(inputdir,outputfile):
	out= np.array([], dtype=np.uint8)
	for name in inputdir:
			#todo: esto es horrible.
			label = name[47:51]

			print(label)
			
			im = Image.open(name)
			rgb_im = im.convert('RGB')
			im = (np.array(rgb_im))

			r = im[:,:,0].flatten()
			g = im[:,:,1].flatten()
			b = im[:,:,2].flatten()

			labelfin= []
			labellist=np.array( list(label),np.uint8)
			
			for charac in np.nditer(labellist):
						charac2=charac+48
						labelfin.append(charac2)
			
			out=np.r_[out,np.array( list(labelfin) + list(r) + list(g) + list(b),np.uint8)]


			
	out.tofile(outputfile)
	return
	
#generate_array(filestraining,"training.bin")
print("training terminado")
#generate_array(fileseval,"verify.bin")
generate_array(filesingle,"single.bin")