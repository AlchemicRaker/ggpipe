# ggpipe
This is a tool for transforming and exporting image data for game development, with tilesheets, maps, sprites, animation, etc.

Sample steps from mockup to data:
1. extract a full palette from the mockup image
    1. this is NES palette data in hex format
1. use a full palette to reduce the mockup's palettes
1. dedup the reduced mockup into a tileset
1. turn the tileset into CHR data
1. generate nametable and attribute table data from the mockup image, tileset, and palette


## Extract A Palette
```
$ ggpipe --get-palette mirai_senshi_clip.png -bg 3F
3F,21,11,1A,27,1A,0A,3F,3F,3F,3F,3F,3F
```

Provide a background color (`-bg`) and a list of input images.  The output is a list of 13 colors, a complete NES palette.  Will fail if a valid palette set cannot be made, possible if any single tile uses more than 3 color + bg color, or if there are more than 4 palettes.  Uses savtool's NES palettes, where 3F is black.

#### Extracted Palette Format
This format is 13 comma separated hexidecimal bytes, one for each color.  The first color is the background color.  The rest of the colors are in groups of three.  Colors 0, 1, 2, and 3 are in palette 0, colors 0, 4, 5, and 6 are in palette 1, etc. 

#### Color Model File
The provided color model in `colormodel.txt` is savtool's NES palettes.  It may be modified to your needs.  Colors in input images are matched to the nearest RGB color found in this file.  Work with multiple color models by using `-color-model mycolormodel.txt`.


## Reduce an Image Palette
```
$ ggpipe mirai_senshi_clip.png -o mirai_grey.png --reduce-palette -palette 3F,21,11,1A,27,1A,0A,3F,3F,3F,3F,3F,3F
(mirai_grey.png is created)
```

Provide a full palette (`-palette`), an input image, and an output image (`-o`).  The output is an image, where all tiles' palettes are stripped.  The first four colors in the color model are used as the "reduced" palette.  By default this is black, dark grey, light grey, and white.


## Generate CHR data
```
$ ggpipe t1g.png t2g.png t3g.png t4g.png --binary -o tset.chr
(tset.chr is created)
```

Provide any number of reduced palette images.  The output is in NES CHR format, with 8 bytes of the low bits, followed by 8 bytes of the high bits of each 8x8 tile.


