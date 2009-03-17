=begin

Wiremap Random Number Generator

This program assists with the depth randomization for the Wiremap.  The Wiremap field, from the birds eye view, is in a trapezoidal shape, like this:

________________________________
\                              /
 \                            /
  \                          /    (Wiremap field)
   \                        /
    \                      /
     \____________________/
      \                  /
       \                /
        \              /
         \            /
          \          /
           \        /
            \      /
             \    /
              \  /
               \/
               x (projector focal point)

The Wiremap functions on the premise that a many slivers of light emit from the focal point of the projector and each intersects a wire.  Each sliver has one and only one wire intersect.

The goal of this program is to evenly distribute the wire plots.  If we used just a vanilla regular randomization function, the final result would have a higher concentration of wires the closer you got to the projector.

This program compensates for projection distortion and redistributes the wires accordingly.

=end 


# Layout Variables

depth = 68.0
maplength = 32.0
d_iterations = 80 # how many different depths there are, conventionally, d of 0 is mapline, 
wire = 256
map_unit = 0.25
d_unit = 0.25

# Program Variables

depth_by_d = Array.new               #  depth_by_d[n] = depth - (n * d_unit)
                                     #  In other words, how far away from the projector is depth line 'n'?
depth_by_ind = Array.new             #  Depth by mapline index - this is what the program is aiming for
width = Array.new                    #  How much distance do you have between the tick marks at given depth?
leveled_width = Array.new            #  leveled_width[n] = width[n] + width[n-1] + ...
normalized_level = Array.new         #  normalized_level normalizes leveled_width to floats between 0 and 1
rand_array = Array.new               #  array of randomized floats, 1 for each wire
level_by_ind = Array.new             #  array of depth indexes, 1 for each wire.
wire_normalized_to_one = Array.new   #  1 / 128 in decimal gradations from 0 to 1.
distribution = Array.new             #  distribution of how many wires per depth tic


output = ""



######################
# Functional Program #
######################

# As a convention I use 'i' for map_indexes, and 'd' as depth indexes.

# Build depth_by_d and width

d = 0
d_iterations.times do
  depth_by_d[d] = depth - (d * d_unit)
  width[d] = (map_unit * depth_by_d[d]) / depth    # trig:   (width[d]/depth_by_d[d]) = map_unit / depth
  d = d + 1
end

# Add all the widths together to get a grand total (actual)

actual_total = 0.0
w = 0
d_iterations.times do
  actual_total = actual_total + width[w]
  w = w+1
end

# Conversion of widths to leveled widths (adding on top of itself)
# Then the normalized_level is a compression to 0.0 to 1.0

lw = 0
d_iterations.times do
  if lw == 0
    leveled_width[lw] = width[lw]
  else
    leveled_width[lw] = leveled_width[lw-1] + width[lw]
  end
  normalized_level[lw] = (leveled_width[lw] / actual_total)
  lw = lw + 1
end

i = 0.0
wire.times do
  wire_normalized_to_one[i] = i / wire
  i = i+1
end

i = 0
wire.times do
  d = 0
  done = false
    while done == false
      if wire_normalized_to_one[i] < normalized_level[d]
        distribution[i] = d
        done = true
      end
      d = d+1
    end
  i = i+1
end

depth_by_ind = distribution.sort_by { rand }

i = 0
wire.times do
  puts depth_by_ind[i]
  i = i+1
end

t = 0
wire.times do
  output = output + "depth_by_ind[" + t.to_s + "] = " + depth_by_ind[t].to_s + "\n"
  t = t+1
end


File.open("final_output128.txt", "w"){|s| s << output}
