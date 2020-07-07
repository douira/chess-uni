# Schach

Software engineering project for the course CS2301 SoSe 2020 at Universität zu Lübeck. Apart from myself, two other students contributed to the code and documentation. Documentation PDFs can be found in the `docs` folder.

Compile the project with `mvn clean compile javafx:jlink` and run with `./target/schach/bin/schach`. The program accepts some command lines flags. See the user manual documents for more information. Code analysis and test reports can be either run through maven or the `fullReport.sh`, `testReport.sh` or `analysisReport.sh` scripts.

## Imports

All import sections (also in tests) should have the following structure. Only use `.*` imports for `.GameTestUtils.*` and `Assertions.*` and nothing else. Try to group imports from `schach.` by subpackage. Leave a empty line between imports from different packages like `java` and `schach`.

```java
package schach.___;

import java.util.___;
...

import schach.___;
...

//a NOPMD is required here because PMD has false positives when importing GameTestUtils
//if it's a test, import of GameTestUtils or IOTestUtils is optional
import org.junit.jupiter.api.Test;
import static schach.game.IOTestUtils.*; //NOPMD
import static schach.game.GameTestUtils.*; //NOPMD
import static org.junit.jupiter.api.Assertions.*;
```

## Writing tests

### Position notation conversion

A Javascript function for converting from integer notation to semi-algebraic notation:

```js
let convert = (str) =>
  Array.from(str.matchAll(/\((\d), ?(\d), ?(\d), ?(\d)\)/gm))
    .map(([_, ...matches]) => matches.map((match) => parseInt(match, 10)))
    .map(
      ([a, b, c, d]) =>
        `"${(a + 9).toString(36)}${b}-${(c + 9).toString(36)}${d}"`
    )
    .join(", ")
```

Example usage:

```js
convert(`
    game.doMove(game.validateMove(setUpMovement(4, 2, 4, 4)));
    game.doMove(game.validateMove(setUpMovement(5, 7, 5, 5)));
    game.doMove(game.validateMove(setUpMovement(4, 4, 4, 5)));
    game.doMove(game.validateMove(setUpMovement(3, 7, 3, 5)));
    game.doMove(game.validateMove(setUpMovement(4, 5, 3, 6)));
    game.doMove(game.validateMove(setUpMovement(2, 7, 2, 5)));
    game.doMove(game.validateMove(setUpMovement(3, 6, 3, 7)));
    game.doMove(game.validateMove(setUpMovement(3, 8, 1, 6)));`)

> ""d2-d4", "e7-e5", "d4-d5", "c7-c5", "d5-c6", "b7-b5", "c6-c7", "c8-a6""
```

also can use this regex for other notations:
`\w+\((\d), (\d)\),(?:\w| )+\((\d), ?(\d)\)`

### Easy check situation for testing

Use this as the initial config in `Board.java` for testing check situations easily. Switch black and white for another similar situation.

```java
placeNewPiece(3, 0, new QueenPiece(Color.WHITE));
placeNewPiece(7, 0, new KingPiece(Color.WHITE));
placeNewPiece(0, 7, new QueenPiece(Color.BLACK));
placeNewPiece(6, 2, new KingPiece(Color.BLACK));
```

### Easy draw situation for testing

Similar to the one above, this can be used for creating draws.

```java
placeNewPiece(3, 0, new QueenPiece(Color.BLACK));
placeNewPiece(7, 0, new KingPiece(Color.BLACK));
placeNewPiece(4, 0, new KingPiece(Color.WHITE));
```

## How to run the checker

Compile the project with `mvn clean compile javafx:jlink` and then run the checker with `java -jar checker.jar "target/schach/bin/schach --no-gui" > log.txt`. Output is written to the specified log file and contains the result of the checker testing the chess engine.
