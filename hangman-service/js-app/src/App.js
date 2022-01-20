import React from 'react';
import './App.css';

class Board extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      wrongGuesses: [],
      letters: ['_','_','_','_','_','_'],
      numberOfLetters: 6,
      numberOfLives: 10,
      isDone: false,
      isWin: false,
      exampleWord: ""
    };
    this.handleChangeNoOfLetters = this.handleChangeNoOfLetters.bind(this);
    this.handleChangeNoOfLives = this.handleChangeNoOfLives.bind(this);
    this.handleRestart = this.handleRestart.bind(this);
    this.performMove = this.performMove.bind(this);
  }

  handleChangeNoOfLetters() {
    let {numberOfLetters} = this.state;

    numberOfLetters += 1
    if (numberOfLetters > 7) {
      numberOfLetters = 5
    }

    this.setState({
      numberOfLetters
    })
    this.handleRestart(numberOfLetters)
  }

  handleChangeNoOfLives() {
      let {numberOfLives, numberOfLetters} = this.state;
      numberOfLives += 1
      if (numberOfLives > 12) {
        numberOfLives = 8
      }
      this.setState({
        numberOfLives
      })
      this.handleRestart(numberOfLetters)
    }

  async performMove(key) {
    if (!this.state.isDone) {
      const response = await fetch("http://localhost:8080/game/" + key, {
        method: "PUT",
        mode: 'cors',
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
      });
      let {numberOfLives, isDone, isWin, exampleWord} = this.state;
      let {state, wrongGuesses, exampleWords} = await response.json();
      console.log(exampleWords)
      if (numberOfLives <= wrongGuesses.length) {
        if (!state.includes("_")) {
          isWin = true;
        } else {
          exampleWord = exampleWords[0]
        }
        isDone = true;
      }

      this.setState({
        wrongGuesses,
        letters: state.split(''),
        isDone,
        isWin,
        exampleWord
      })
    }
  }

  async handleRestart(numberOfLetters) {
    await fetch("http://localhost:8080/game/" + numberOfLetters, {
      method: "POST",
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });

    this.setState({
      wrongGuesses: [],
      letters: new Array(numberOfLetters).fill('_'),
      isDone: false
    })
  }

  async handleGetState() {
      const response = await fetch("http://localhost:8080/game", {
        method: "GET",
        mode: 'cors',
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
      });
      let {state, wrongGuesses} = await response.json();

      this.setState({
        wrongGuesses,
        letters: state.split('')
      })
    }

  render() {
    return (
      <div>
        <div className="word">
          {this.state.letters.map( (letter, index) => {
            return <div key={index} className='letter'>{letter}</div>
          })}
        </div>
        <div className="wrong-guesses">
          <div className="label">Incorrect Answers ({this.state.wrongGuesses.length}): </div>
          {this.state.wrongGuesses.map( (letter, index) => {
            return <div key={index} index={index} className='wrong-guess'>{letter}</div>
          })}
        </div>
        <div className="buttons">
          <div className="basic-button restart" onClick={() => this.handleRestart(this.state.numberOfLetters)}>Restart</div>
          <div className="basic-button number-of-letters"  onClick={() => this.handleChangeNoOfLetters()}>Letters: {this.state.numberOfLetters}</div>
          <div className="basic-button lives"  onClick={() => this.handleChangeNoOfLives()}>Lives: {this.state.numberOfLives}</div>
        </div>
        {!this.state.isDone || <div className="result">{this.state.isWin ? "YOU WON!!!" : "You Lost (Answer was: " + this.state.exampleWord + ")"}< /div>}
      </div>
    );
  }

  componentDidMount(){
      this.handleGetState()
      let y = this;
      function handleKeyDown(e) {
        if (e.keyCode >= 65 && e.keyCode <= 90) {
          y.performMove(e.key);
        }
      }
      document.addEventListener('keydown', handleKeyDown);
      return function cleanup() {
        document.removeEventListener('keydown', handleKeyDown);
      }
  }
}

class App extends React.Component {
  render() {
    return (
      <div className="game">
        <div className="game-board">
          <Board />
        </div>
      </div>
    );
  }
}

export default App;
