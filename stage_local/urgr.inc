# 2011-06-07 Sebastian Rockel
# Hokuyo URG-04LX definition

define laser240 ranger
(
  sensor (
    range [ 0.02 5.6 ]
    fov 240.0
    samples 682
    #watts 2.0
    color_rgba [ 0 0 1 0.15 ]
  )

  # generic model properties
  model (
    color "blue"
    size [ 0.05 0.05 0.1 ]	
    pose [ 0 0 -0.2 0 ] # to be set in robot definition
  )
)
