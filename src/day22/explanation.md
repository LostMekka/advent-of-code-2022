The first part of this puzzle is quite straight forward, but the second part took me a while to wrap my head around. Here is the explanation of the algorithm I ended up implementing:


There are 3 important points to look at:
- `p1` is the point you are standing on. It also has the direction you want to go (`d1`) associated with it.
- `p2` is the "pivot point". It is found by turning left from the direction you are facing and walking forward until you reach a point that is not in the same cube face you are standing in.
- `p3` is the "target point". It is found by rotation `p1` 90 degrees to the left around `p2`. It also has a direction `d2` associated with it, which is just `d1`, but also rotated.

```
                      d2               
           [][][][]  /                 
           [][][]p3 <                  
           [][][][]        d1          
           [][][][]       /            
                         ^             
  [][][][] [][][]p2 [][]p1[] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
                                       
                    [][][][]           
                    [][][][]           
                    [][][][]           
                    [][][][]           
``` 
In This example, we stand at `p1/d1` and `p3/d2` is where we would end up on when walking forward. This is the easiest case, where p3 is on the cube, so we can end our search here.

The second case occurs, when `p3` is not on the cube, but `p2` is:
```
           [][][][]                    
           [][][][]       p3 <         
           [][][][]                    
           [][][][]                    
                                  ^    
  [][][][] [][][][] [][][]p2 [][]p1[]  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
                                       
                    [][][][]           
                    [][][][]           
                    [][][][]           
                    [][][][]           
``` 
In this case, we first need to find the pairing for the face of `p2`. for that, we use a stack. We push `p2/d1` on the stack and try to solve for this one first:
```
           [][][][]                    
           [][][]p3 <                  
           [][][][]                    
           [][][][]                    
                           ^      ^    
  [][][][] [][][]p2 [][][]p1 [][]s1[]  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
                                       
                    [][][][]           
                    [][][][]           
                    [][][][]           
                    [][][][]           
``` 
I denote the points on the stack as `s1` through `s9`, so we don't lose track of them. We now have new points `p1/d1`, `p2` and `p3/d2` for the current element of the stack. This is the easy case from before, so we can solve it. when we are not solving for the last element of the stack though, it gets a bit more complicated. The solved element gets popped from the stack and all remaining elements also rotate around `p2`. This is like taking the two faces of teh cube out of their position and pasting it onto the face pf `p3` like so:
```
                    [][][][]           
                  < s1[][][]           
                    [][][][]           
                    [][][][]           
                                       
           [][][][] [][][][]           
           [][][]p3 p1[][][]           
           [][][][] [][][][]           
           [][][][] [][][][]           
                                       
  [][][][] [][][]p2                    
  [][][][] [][][][]                    
  [][][][] [][][][]                    
  [][][][] [][][][]                    
                                       
                    [][][][]           
                    [][][][]           
                    [][][][]           
                    [][][][]           
``` 
Note that we also needed to shift each point on the stack one position into the opposite of `d2`, so our cube faces align properly again. (Note, that in reality, we only rotate elements on the stack and not the cube faces on the map. This is just for visualization)

Now our stack is only one element high and we find ourselves at the easy-to-solve first case:
```
                    [][][][]           
                  < p1[][][]           
                    [][][][]           
                    [][][][]           
              v                        
           []p3[][] p2[][][]           
           [][][][] [][][][]           
           [][][][] [][][][]           
           [][][][] [][][][]           
                                       
  [][][][] [][][][]                    
  [][][][] [][][][]                    
  [][][][] [][][][]                    
  [][][][] [][][][]                    
                                       
                    [][][][]           
                    [][][][]           
                    [][][][]           
                    [][][][]           
``` 
So we found that if you go up from `AA`, you end up entering at `BB` facing downwards.
```
              v                        
           []BB[][]                    
           [][][][]                    
           [][][][]                    
           [][][][]                    
                                  ^    
  [][][][] [][][][] [][][][] [][]AA[]  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][][]  
                                       
                    [][][][]           
                    [][][][]           
                    [][][][]           
                    [][][][]           
``` 

There is one third case though, where neither `p3` nor `p2` are on the cube:
```
           [][][][]                               
           [][][][]                               
           [][][][]                               
           [][][][]                               
                                   p2     p3      
  [][][][] [][][][] [][][][] [][][][]      ^      
  [][][][] [][][][] [][][][] [][][][]             
  [][][][] [][][][] [][][][] [][][]p1 >           
  [][][][] [][][][] [][][][] [][][][]             
                                                  
                    [][][][]                      
                    [][][][]                      
                    [][][][]                      
                    [][][][]                      
``` 
In that case, we just move `p2` inside the cube face again and push this point onto the stack, alongside `d2`:
```
                                       
           [][][][]       p3 <         
           [][][][]                    
           [][][][]                    
           [][][][]                    
                                    ^  
  [][][][] [][][][] [][][]p2 [][][]p1  
  [][][][] [][][][] [][][][] [][][][]  
  [][][][] [][][][] [][][][] [][][]s1 >
  [][][][] [][][][] [][][][] [][][][]  
                                       
                    [][][][]           
                    [][][][]           
                    [][][][]           
                    [][][][]           
``` 
We can solve normally from here.

This algorithm finds the target position and rotation for any position / direction pair, where the position is on the edge of a cube face and the direction points into nothingness. It was quite fun to come up with it :)
