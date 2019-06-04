# ggpipe
This is a tool for transforming and exporting image data for game development, with tilesheets, maps, sprites, animation, etc.

Sample steps from mockup to data:
1. extract a palette from the mockup image
    1. this is NES palette data in hex format
1. create a grey mockup from the mockup image and palette
1. dedup the grey mockup into a tileset
1. turn the tileset into CHR data
1. generate nametable and attribute table data from the mockup image, tileset, and palette


## Extract A Palette
`ggpipe image1 image2 imagen --getpalette -bg F3`

Provide a background color and a list of input images.  The output is a list of 10 colors, a complete NES palette.  Will fail if a valid palette set cannot be made, possible if any single tile uses more than 3 color + bg color, or if there are more than 4 palettes.  Uses savtool's NES palettes, where 3F is black.




