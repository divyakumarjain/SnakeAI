
#Credits

https://neuro.cs.ut.ee/demystifying-deep-reinforcement-learning/
https://medium.com/emergent-future/simple-reinforcement-learning-with-tensorflow-part-0-q-learning-with-tables-and-neural-networks-d195264329d0

```shell script
#without saved model
java org.divy.ai.snake.application.SnakeRLApplicationKt snake-board-rl-qneural-network --learning-rate 0.001 --num-episodes 10000 --board-width 20 --board-height 20 --delay-in-millis 1 --random-factor 0.9 --num-food-drops 10 --experience-buffer-size 10000 --use-saved-model=./DNSnakeBoard.model --use-eight-direction --random-decay=0.99

#with saved model
java org.divy.ai.snake.application.SnakeRLApplicationKt snake-board-rl-qneural-network --learning-rate 0.001 --num-episodes 10000 --board-width 20 --board-height 20 --delay-in-millis 1 --random-factor 0.9 --num-food-drops 10 --experience-buffer-size 10000 --use-eight-direction --random-decay=0.99
```
