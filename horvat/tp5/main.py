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
from granular_beeman import GranularBeeman
from random import random
import time

colored_traceback.add_hook()

def init():
	folders = ['output']
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
		'config': './config/config.json',
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

def Q_post_process(Q,QT):
	Q_rev = Q
	Q_rev.reverse()
	i = 0
	avg = 0.0
	found = False
	while not found:
		avg = (avg * i + Q_rev[i]) / (i + 1.0)
		print i
		if i == len(Q_rev) - 1 or (i > 5 and abs(Q_rev[i+1] - avg) > avg * 0.01):
			found = True
		i+=1 
	return (Q[-i:],QT[-i:])

def start():

	positions_str = {"v":""}
	beeman = GranularBeeman(N = N, L = L, W = W, D = D, d_min = d_min, d_max = d_max, g = g, kT = kT, kN = kN, m = m, tf = tf, dt = dt, dN = dN)

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
	Q = []
	Q2 = []
	summ2 = 0
	Q3 = []
	summ3 = 0
			
	for index in xrange(1,int(tf/dt)+1):
		t = index * dt
		method.loop(t)
		if (index % int(dt2/dt) == 0):
			print t
			positions_str["v"] = positions_str["v"] + method.get_info(i = index, L = L, W = W, D = D)
			energy.append(method.get_energy_sum())
			energy_var.append(energy[0] if len(energy) == 1 else energy[-1] - energy[-2])


	#plot(analitic.r_history,verlet.r_history,beeman.r_history, gear.r_history)

	with open('output/data.txt', 'w') as outfile:
		outfile.write(positions_str["v"])

	with open('output/energy.txt', 'w') as outfile:
		outfile.write(str(energy))

	with open('output/energy_var.txt', 'w') as outfile:
		outfile.write(str(energy_var))


	for i in xrange(0,len(method.Q)):
		(Q,QT) = Q_post_process(method.Q[i],method.Qt[i][1:])
		with open('output/Q_'+str(i)+'.txt', 'w') as outfile:
			outfile.write(str(Q))
		with open('output/Qt_'+str(i)+'.txt', 'w') as outfile:
			outfile.write(str(QT))
		with open('output/Q'+str(i)+'.txt', 'w') as outfile:
			outfile.write(str(method.Q[i]))
		with open('output/Qt'+str(i)+'.txt', 'w') as outfile:
			outfile.write(str(method.Qt[i][1:]))

def main():

	arguments = parse_arguments()

	init()

	with open(arguments['config']) as data_file:
		global data
		data = json.load(data_file)

	pprint.pprint(data)

	global N,L,W,D,d_min,d_max,g,tf,dt,kT,kN,m,dt2,case,dN

	N = data["N"]
	L = data["L"]
	W = data["W"] 
	D = data["D"] 
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
	dN = data["dN"]
	
	start_time = time.time()

	start()

	run_time = time.time() - start_time

	print run_time


if __name__ == '__main__':
	main()