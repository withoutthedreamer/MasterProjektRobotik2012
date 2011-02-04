include "pioneer.inc"
#include "utm30lx.inc"
include "urgr.inc"
include "platte.inc"
include "laptop.inc"

define bobsblobfinder blobfinder
(
  colors_count 3
  colors [ "red" "green" "black" ]
  fov 1.047196667 # 60 degrees = pi/3 radians
  range 1
  #range_max 5
  # camera parameters
  #image [160 120]		#resolution
  image [80 60]		#resolution
  
)

define bobsgripper gripper
(
  pose [0.23 0.000 -0.20 0.000]
  color "gray"
)

define robot fancypioneer2dx
(		  
  laser240( pose [0.13 0 -0.2 0] )
  #utm30lx( pose [0.13 0 -0.2 0] ) 
  platte()
  laptop()
  bobsblobfinder()
  bobsgripper()

  obstacle_return 1
  ranger_return 1
  blob_return 0
)

define blob model
(
  size [ 0.3 0.3 0.0]
  boundary 0
  gui_move 1
  gripper_return 1
  obstacle_return 0
  laser_return 0
  ranger_return 0
  blob_return 1
)
