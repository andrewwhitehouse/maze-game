# Amazing Dojo

This program is a game where players compete to write functions in
Clojure that can find their way through randomly generated mazes.

## Instructions

### The maze solving funtion

The maze solving function must take 5 arguments.
```
  (fn [n-view e-view s-view w-view path] 'your-code-here)
```

Each view is a vector containing 3 numbers. The first number tells the
function the length of the path to the next wall in that
direction. For example if the next northerly wall is right next to the
current position then n-view's first number will be 0. If the player
has room to move north 3 spaces then n-view's first number will
be 3. The second and third values in each vector are the number of
side passages along the path, leading to the right and to the left
respectively.

Picture the scene - you are in a maze facing south - about 10 metres
directly ahead of you there is a wall. There is a wall immediately to
your left (east) and another passage to your right (west). Up ahead,
before the wall you can also make out another passage off to the
left. In this situation the s-view argument to the function would be
[5 1 1]. The 5 is the distance to the next wall (assuming 2 metres for
each space). The first 1 is the passage immediately to your right. The
second 1 is the passage up ahead leading left. Note that you are not
told how far away any side passages are, only how many of them there
are.

The path argument is a list of all the moves the player has made so far.

The function does not get told its absolute position within the maze,
nor the size of the maze, but know the maze is a square of between 10
and 20 units on each side. You begin at position 0 (north-west)
and succeed you reach the south-east corner, e.g. position 99 in a
10x10 maze.

Each turn, the function must return a keyword, one of :n, :e, :s or :w.

If your move is invalid the function will be called again with the
same inputs, except one try will have been used.

** Running the function
*** Running against a server
To upload a function to a running server just follow the upload link
on the server's index page. You can upload a whole file with a
namespace, or just paste in a single form.

When pasting code directly, the server expects a single form that
evaluates to a function. This means that any helper functions must be
defined in a letfn form that returns the main function. For example:

```
  (letfn [(helper-fn [arg] 'helper-code)
          (another-helper [arg] 'more-code)]
    (fn [n-view e-view s-view w-view path] 'your-code-which-calls-helpers))
```

When uploading a file, the server expects to find a function called
solver in the namespace provided.

```
  (ns team-42)
  
  (defn solver [n-view e-view s-view w-view path]
    'your-code)
```

### Running manually
To run the code first clone the project.

`$ git clone https://github.com/bloat/maze-game.git`

Then start a repl - you can use swank if you prefer at this point.

```
$ cd maze-game
$ lein deps
$ lein repl
user=> (require 'maze.www)
```

You can generate a maze using the maze-gen function.
```
  (maze.gen/maze-gen)
```

You can print the maze using the draw-maze function.
```
  (use 'maze.gen)
  (use 'maze.draw)
  (draw-maze (maze-gen))
```

You can run your maze solving function using the play-maze
function. The first argument is the maze. The second is your
function. The third is the start point, use 0 here, which is the most
north-westerly location. The fourth argument is the number of turns
your function will get before giving up. The game server allows 5000
turns. The last argument is your path so far, so start with an empty
vector.
```
  (use 'maze.gen)
  (use 'maze.play)
  (play-maze (maze-gen) your-fn 0 1000 [])
```

### Running your own server
To start the web server start a repl as above. Use the following code
in the repl.

```
  (require 'maze.www)
  (in-ns 'maze.www)
  (use 'ring.adapter.jetty)
  (.start (Thread. #(run-jetty app {:port 3003})))
```

This will start the server and allow competitors to upload their
entries, but the server will not actually run any matches until the
game is started. To do this you can visit

```
http://<yourhost>/control
```

You can also start and stop the game from the repl.
```
  (in-ns 'maze.controller)
  (start)
  (stop)
```

### Heroku setup

#### Install

<https://devcenter.heroku.com/articles/getting-started-with-clojure?singlepage=true>

#### Init

`heroku login`

`heroku git:remote -a amazing-clojure`

`heroku plugins:install heroku-builds`

#### Update app

`git push heroku master`

#### Kill builds

heroku builds:cancel -a amazing-clojure

#### Stop App

heroku ps:scale web=0

#### App location

<https://amazing-clojure.herokuapp.com/>

### Security
There is no protection against malicious usage. Any function that is
uploaded will be executed! Run the server on a throw away VM.

# LICENSE 
This file is part of Amazing Dojo.

Amazing Dojo is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Amazing Dojo is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Amazing Dojo. If not, see <http://www.gnu.org/licenses/>.
