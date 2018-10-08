import React, {Component} from 'react';
import { render } from 'react-dom';
import * as bs from 'bootstrap-css';
import _ from 'lodash';
import { Button } from 'react-bootstrap';
import randomcolor from 'randomcolor';

let input = require('../../output/neighbours.json');
console.log(input);

import './App.css';

class App extends Component {

	constructor(props) {
		super(props);

		this.refresh = this.refresh.bind(this);
		this.onShowNeighbours = this.onShowNeighbours.bind(this);
		this.hideNeighbours = this.hideNeighbours.bind(this);
		this.state = { i: 0, neighbours: [] };
	}

	refresh(e) {
		e.preventDefault();
		input = require('../../output/neighbours.json');
		console.log(input);
		this.setState({ i: this.state.i + 1 });
	}

	onShowNeighbours(e, i) {
		e.preventDefault();
		this.setState({ neighbours: input.neighbours[i][1] })
	}

	hideNeighbours(e) {
		this.setState({ neighbours: [] });
	}

  render() {
    return (
      <div>
      	<div>
	      	<Button onClick={this.refresh}>Refrescar</Button>
	      </div>
	      <div style={{margin:'10% auto 0', width: input.world.L * 32}}>
	        <svg xmlns="http://www.w3.org/2000/svg" height={input.world.L * 32} width={input.world.L * 32}>
	        	{_.times(input.world.M + 1, (i) =>
	        		<line
	        			key={i}
	        			x1={input.world.L / input.world.M * i * 32}
	        			x2={input.world.L / input.world.M * i * 32}
	        			y1={0}
	        			y2={input.world.L * 32}
	        			style={{stroke:'red', strokeWidth: 2}}
	        			/>
	        		)
	        	}
	        	{_.times(input.world.M + 1, (i) =>
	        		<line
	        			key={i}
	        			y1={(input.world.L / input.world.M) * i * 32}
	        			y2={(input.world.L / input.world.M) * i * 32}
	        			x1={0}
	        			x2={input.world.L * 32}
	        			style={{stroke:'red', strokeWidth: 2}}
	        			/>
	        		)
	        	}
	        	{_.map(input.particles, (particle, i) =>
	        		<circle
	        			className={["circle", _.includes(this.state.neighbours, i) && "neighbour"].join(' ')}
	        			key={i}
	        			cx={particle.x * 32}
	        			cy={particle.y * 32}
	        			r={(particle.r + 0.1) * 32}
	        			onClick={e => this.onShowNeighbours(e, i)}
	        			onMouseEnter={e => this.onShowNeighbours(e, i)}
	        			onMouseLeave={this.hideNeighbours}
	        			/>)}
	        </svg>
	       </div>
      </div>
    );
  }
}

render(<App />, document.getElementById('root'));
