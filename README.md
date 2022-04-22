Spring Challenge 2022

Source code for CodinGame's Spring Challenge 2022 event.

https://www.codingame.com/contests/spring-challenge-2022/

Community starter AIs are located here:

https://github.com/CodinGame/SpringChallenge2022/tree/main/starterAIs

Introduction video by Mathis Hammel:

https://youtu.be/MyHjWftmMfQ

### [Brutaltester](https://github.com/dreignier/cg-brutaltester) compatibility

In addition to the usual modifications I had to compile some typescript files for the viewer

```
cd src/main/resources/view
npm install
npm start
rm -fr node_modules
```

If you don't remove the node_modules directory then maven will add the whole lot to the jar file.