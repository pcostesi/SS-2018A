import json
import ipdb
import pprint
import colored_traceback
import sys
import os
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
from src.genetic_algorithm import GeneticAlgorithm
from src.archer import Archer
from src.warrior import Warrior
from src.assassin import Assassin
from src.defender import Defender
import getopt

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
	print('usage: python main.py [options]')
	print('-h --help:	 print this screen')
	print('-c --config=:  configuration file')

def parse_arguments():
	arguments = {
		'config': './config/config.json',
	}
	try:
		opts, args = getopt.getopt(sys.argv[1:], 'hc:tn', ['help', 'config='])
	
	except getopt.GetoptError as err:
		print str(err)  # will print something like "option -a not recognized"
		usage()
		sys.exit(2)
	
	for o, a in opts:
		if o in ('-h', '--help'):
			usage()
			sys.exit()
		elif o in ('-c', '--config'):
			arguments['config'] = a
		else:
			assert False, 'unknown option `' + o + '`' 

	return arguments

def main():

	arguments = parse_arguments()
	
	with open(arguments['config']) as data_file:
		global data
		data = json.load(data_file)

	pprint.pprint(data)


	L = data["L"] or 2
	N = data["N"] or 10
	particles = []
	
	for part in xrange(1, N):
		x = random.randint(1, L)
		y = random.randint(1, L)
		particles.append([x,y])
	
	// OUT