import json
import ipdb
import pprint
import colored_traceback
import sys
import os
import getopt
import matplotlib.pyplot as plt
import numpy as np
from math import exp
from math import cos
from math import sin
from math import hypot
from granular_beeman_b import GranularBeeman
from random import random
import time

colored_traceback.add_hook()

def init():
	folders = ['output_b']
	for folder in folders:
		folder_path = os.path.join('.', folder)
		if not os.path.exists(folder_path):
			os.makedirs(folder_path)

	for folder in folders:
		folder_path = os.path.join('.', folder)
		for file in os.listdir(folder):
			file_path = os.path.join(folder, file)
			if os.path.isfile(file_path):
				os.remove(file_path)

def usage():
	print('usage:	python main.py')

def parse_arguments():
	arguments = {
		'config': './config/config_b.json',
	}
	try:
		opts, args = getopt.getopt(sys.argv[1:], 'hc:tn', ['help'])
	
	except getopt.GetoptError as err:
		print str(err)  # will print something like "option -a not recognized"
		usage()
		sys.exit(2)
	
	for o, a in opts:
		if o in ('-h', '--help'):
			usage()
			sys.exit()
		else:
			assert False, 'unknown option `' + o + '`' 

	return arguments

def start():

	positions_str = {"v":""}
	beeman = GranularBeeman(N = N, L = L, W = W, D = 0.0, d_min = d_min, d_max = d_max, g = g, kT = kT, kN = kN, m = m, tf = tf, dt = dt)

	method = beeman

	if case != "None":
		for particle in method.particles:
			if case == "Down":
				pass 
			if case == "Right":
				particle.gx = particle.g
				particle.a = {"x" : particle.gx,"y" : 0.0}
				particle.a_prev = {"x" : particle.gx,"y" : 0.0}
				particle.g = 0.0
			if case == "Diag":
				particle.v = {"x" : 0.050,"y" : -0.020}
				particle.a = {"x" : 0.0,"y" : 0.0}
				particle.a_prev = {"x" : 0.0,"y" : 0.0}
				particle.g = 0.0
				y = random() * L /8.0 + L *3/8.0
				x = 3/4.0 * W  + particle.id * 0.001
				
				particle.r = {"x" : x,"y" : y}
			if case == "Test":

				found = False
				while not found:

					y = random() * (L - particle.rad) + particle.rad 
					x = 3/4.0 * W  + particle.id * 0.01
					particle.r = {"x" : x,"y" : y}
					for other_particle in method.particles:
						if other_particle.id == particle.id:
							found = True
							break
						if hypot(particle.r["x"] - other_particle.r["x"], particle.r["y"] - other_particle.r["y"]) < other_particle.rad + particle.rad:
							break

				
			#particle.r_corr= particle.get_initial_r_corr()

	energy = []
	energy_var = []
	end = False
	minn = False
	index = 1
	while not end:
		t = index * dt
		method.loop()
		if (index % int(dt2/dt) == 0):
			print t
			positions_str["v"] = positions_str["v"] + method.get_info(i = index, L = L, W = W)
			energy.append(method.get_energy_sum())
			energy_var.append(energy[0] if len(energy) == 1 else energy[-1] - energy[-2])
			#if len(energy) > 100 and max (energy[-100:]) < 0.0001:
			#	end = True
			#	break

			actual_energy = energy[-1]
			if minn:
			
				print int(round(time_equal/dt2))
				if abs(actual_energy - minn) < min_error:
					count +=1
					end = False if count < int(round(time_equal/dt2)) else True
				else:
					minn = actual_energy
					count = 0
			else:
				minn = actual_energy
				count = 0
			print actual_energy
			print count
			
			if index % 1000 == 0:
				with open('output_b/data'+str(index)+'.txt', 'w') as outfile:
					outfile.write(positions_str["v"])
		index +=1


	#plot(analitic.r_history,verlet.r_history,beeman.r_history, gear.r_history)

	with open('output_b/data.txt', 'w') as outfile:
		outfile.write(positions_str["v"])

	with open('output_b/energy.txt', 'w') as outfile:
		outfile.write(str(energy))

	with open('output_b/energy_var.txt', 'w') as outfile:
		outfile.write(str(energy_var))

	with open('output_b/max_h.txt', 'w') as outfile:
		outfile.write(str(max(map(lambda particle: particle.r["y"] + particle.rad, method.particles))))

	with open('output_b/max_force.txt', 'w') as outfile:
		outfile.write(str(method.max_force))


def plot(analitic_array, verlet_array, beeman_array, gear_array):
	legends = ['Analitic','Velvet','Beeman','Gear']
	plt.plot(xrange(len(analitic_array)), analitic_array)
	plt.plot(xrange(len(analitic_array)), verlet_array)
	plt.plot(xrange(len(analitic_array)), beeman_array)
	plt.plot(xrange(len(analitic_array)), gear_array)
	plt.legend(legends)
	plt.xlabel('t')
	plt.ylabel('Y')
	_max = 1.0
	_min = -1.0
	_delta = (_max - _min) / 15
	_delta = 0.01 if _delta == 0 else _delta
	plt.yticks(np.arange(_min - _delta, _max + _delta, _delta))
	plt.savefig(os.path.join(os.getcwd(),"output_b_particle/i.png"))
	plt.close()

def main():

	arguments = parse_arguments()

	init()

	with open(arguments['config']) as data_file:
		global data
		data = json.load(data_file)

	pprint.pprint(data)

	global N,L,W,D,d_min,d_max,g,tf,dt,kT,kN,m,dt2,case,min_error,time_equal

	N = data["N"]
	L = data["L"]
	W = data["W"]
	d_min = data["d_min"]
	d_max = data["d_max"]
	g = data["g"]
	tf = data["tf"]
	dt = data["dt"]
	dt2 = data["dt2"]
	kT = data["kT"]
	kN = data["kN"]
	m = data["m"]
	case = data["case"]
	min_error = data["min_error"]
	time_equal = data["time_equal"]
	
	start_time = time.time()

	start()

	run_time = time.time() - start_time

	print run_time


if __name__ == '__main__':
	main()
