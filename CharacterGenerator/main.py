# -*- coding: utf-8 -*-   
  
#-------------------------------- Imports ------------------------------#  
  
# Import python imaging libs  
from PIL import Image  
from PIL import ImageDraw  
from PIL import ImageFont  

# Import font utils
from fontTools.ttLib import TTFont
  
# Import operating system lib  
import os  
  
# Import random generator  
from random import randint  

#-------------------------------- Utils ------------------------------#  

def charInFont(unicode_char, font):
    for cmap in font['cmap'].tables:
        if cmap.isUnicode():
            if ord(unicode_char) in cmap.cmap:
                return True
    return False
  
#-------------------------------- Cleanup ------------------------------#  
                      
def Cleanup(out_dir):      
    # Delete ds_store file  
    if os.path.isfile(font_dir + '.DS_Store'):  
        os.unlink(font_dir + '.DS_Store')  
          # Delete all files from output directory  
    for file in os.listdir(out_dir):  
        file_path = os.path.join(out_dir, file)  
        if os.path.isfile(file_path):  
            os.unlink(file_path)  
    return

#-------------------------------- Read File ------------------------------#

def ReadCharacters():
     charactersr = []
     with open(charlist, encoding="utf8") as openfileobject:
            for line in openfileobject:
                charactersr.append(line[0])
     openfileobject.close()
     return charactersr
  
#--------------------------- Generate Characters -----------------------#  
  
def GenerateCharacters():  
    # Counter  
    k = 1

    characterslist= ReadCharacters()
    
    # Process the font files  
    for dirname, dirnames, filenames in os.walk(font_dir):  
        filenum = 0
        # For each font do  
        for filename in filenames:  
            # Get font full file path  
            font_resource_file = os.path.join(dirname, filename)  
            filenum = filenum + 1

            charnum = -1
            # For each character do  
            for char in characterslist:
                charnum = charnum + 1
                #Does the character exist in this font?
                print(font_resource_file)
                if charInFont(char, TTFont(font_resource_file)):
                    # For each font size do  
                    for font_size in font_sizes:  
                       if font_size > 0:  
                         # For each background color do  
                         for background_color in background_colors:  

                            character = char
				  
                            # Create character image :   
                            # Grayscale, image size, background color  
                            char_image = Image.new('L', (image_size, image_size), background_color)  
				  
                            # Draw character image  
                            draw = ImageDraw.Draw(char_image)  
				  
                            # Specify font : Resource file, font size  
                            font =  ImageFont.truetype(font_resource_file, font_size)  
				  
                            # Get character width and height  
                            (font_width, font_height) = font.getsize(character)  
				  
                            # Calculate x position  
                            x = (image_size - font_width)/2  
				  
                            # Calculate y position  
                            y = (image_size - font_height)/2  
				  
                            # Draw text : Position, String,   
                            # Options = Fill color, Font  
                            draw.text((x, y), character, (245-background_color) + randint(0, 10) , font=font)  
						   
						   
                            if (filenum > (len(filenames)/3.0) ):
                                                out_dir = out_dir1
                            else :
                                                out_dir = out_dir2
						  
                            # Final file name                      
                            file_name = out_dir + format(charnum, '04d') + '_' + str(k) + '_' + filename + '_fs_' + str(font_size) + '_bc_' + str(background_color) + '.png'
                            file_namea = out_dir + format(charnum, '04d') + '_' + str(k) + '_' + filename + '_fs_' + str(font_size) + '_bc_' + str(background_color) + 'a.png'
                            file_nameb = out_dir + format(charnum, '04d') + '_' + str(k) + '_' + filename + '_fs_' + str(font_size) + '_bc_' + str(background_color) + 'b.png'  

                            # Save image  
                            char_image.save(file_name)

                            im2 = char_image.convert('RGBA')

                            # Save image rotated
                            rot1= im2.rotate(5)
                            fff = Image.new('RGBA', rot1.size, (255,)*4)
                            out = Image.composite(rot1, fff, rot1)
                            out.convert(im2.mode).save(file_namea)
						   
                            rot2= im2.rotate(-5)
                            out2 = Image.composite(rot2, fff, rot2)
                            out2.convert(im2.mode).save(file_nameb)
						  
                            # Increment counter  
                            k = k + 1
                       
    return  
  
  
  #------------------------------- Input and Output ------------------------#  
  
# Directory containing fonts  
font_dir = 'fonts'  
  
# Output
out_dir1 = 'filea/'
out_dir2 = 'fileb/'

# Char list
charlist= 'lista.txt'
  
#---------------------------------- Colors -------------------------------#  
  
# Background color  
white_colors = (215, 225, 235, 245)  
black_colors = (0, 10, 20, 30)  
gray_colors = (135, 145, 155)  
  
#background_colors = white_colors + black_colors + gray_colors
background_colors = [255]
          
#----------------------------------- Sizes -------------------------------#  
  
# Character sizes  
font_sizes = [59]
          
# Image size  
image_size = 64  
  
#----------------------------------- Main --------------------------------#  
  
#Check if fonts directory exist
if not os.path.isdir(font_dir):
     sys.exit("A directory named fonts should exist. It should content all the fonts you want to use to generate images")

# Create directories if they don't exist
if not os.path.exists(out_dir1):
     os.makedirs(out_dir1)
if not os.path.exists(out_dir2):
     os.makedirs(out_dir2)  

# Do cleanup  
Cleanup(out_dir1)  
Cleanup(out_dir2)



# Generate characters  
GenerateCharacters()  
