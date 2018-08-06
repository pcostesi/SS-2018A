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
from analitic import Analitic
from beeman import Beeman
from verlet import Verlet
from gear import Gear5

colored_traceback.add_hook()

m = 0.1
k = 0
gamma = 0
dt = 0.01
gamma_div_2m = 0

def init():
	folders = ['output_particle']
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
		'config': './config_particle/config.json',
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

def a_function(r,v):
	return -k/m*r - gamma/m *v

def r_analitic_function(t):
	return exp(- gamma_div_2m * t ) * cos ( (( (k/m) - gamma_div_2m **2) ** 0.5) * t)

def v_analitic_function(t):
	return - gamma_div_2m * exp(- gamma_div_2m * t ) * cos ( (( (k/m) - gamma_div_2m **2) ** 0.5) * t) + exp(- gamma_div_2m * t ) * (-1 ) * sin ( (( (k/m) - gamma_div_2m **2) ** 0.5) * t) *  (( (k/m) - gamma_div_2m **2) ** 0.5)	

def start(dt = dt):
	analitic = Analitic(r = 1, v = - gamma_div_2m, r_function = r_analitic_function, v_function = v_analitic_function, a_function = a_function)
	verlet = Verlet(r = 1, v = - gamma_div_2m, a_function = a_function, dt = dt)
	beeman = Beeman(r = 1, v = - gamma_div_2m, a_function = a_function, dt = dt)
	gear = Gear5(r = 1, v = - gamma_div_2m, a_function = a_function, dt = dt, k = k, m = m)
	strr = ""
	for index in xrange(1,int(tf/dt)+1):
		t = index * dt
		#import ipdb; ipdb.set_trace()
		analitic.loop(t)
		verlet.loop()
		beeman.loop()
		gear.loop()
		#strr+= (str(verlet[R])) + '\t' + str(analitic[R]) + '\t' + (str(verlet[V])) + '\t' + str(analitic[V]) + '\t' + (str(verlet[A])) + '\t' + str(analitic[A]) + '\n'

	 
	plot(analitic.r_history,verlet.r_history,beeman.r_history, gear.r_history)

	with open('output_particle/data.txt', 'w') as outfile:
		methods = [verlet,beeman,gear]
		outfile.write(reduce(lambda accum,elem: accum + str(elem.__class__) + str(elem.get_error(analitic.r_history)) + " ,",methods,""))

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
	plt.savefig(os.path.join(os.getcwd(),"output_particle/i.png"))
	plt.close()

def main():

	arguments = parse_arguments()

	init()

	with open(arguments['config']) as data_file:
		global data
		data = json.load(data_file)

	pprint.pprint(data)

	global m,k,gamma,tf,dt, gamma_div_2m

	m = data["m"] 
	k = data["k"]
	gamma = data["gamma"]
	tf = data["tf"]
	dt = data["dt"]
	gamma_div_2m = gamma / ( 2 * m)
	
	start(dt = dt)


if __name__ == '__main__':
	main()