The key to this puzzle is to separate the simulation of the blizzards and the path finding search.

The blizzards move periodically, depending on their path length:
- Horizontally moving blizzards repeat their position after `width` steps.
- Vertically moving blizzards repeat their position after `height` steps.
- So after `width * height` steps, everything repeats.

With this in mind, the walkable positions can be pre-computed. This produces a 3D space where the dimensions are:
- x: left / right
- y: forward / backward
- time: up /down

The search then just finds a path through this 3D space, where every move not only moves in the x/y plane, but also must move one voxel up. Since the pre-computed slices repeat, these blocks of slices can be stacked onto each other, creating an arbitrarily high tower for the search.

In the end, I used an A* implementation I built for AoC 2021 to efficiently find the path through this tower.
