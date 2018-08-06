import matplotlib.pyplot as plt
import numpy as np
import getopt
import sys
import os
import json
import pprint
from mpl_toolkits.mplot3d import Axes3D

def init():
	folders = ['output_planet']
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

def parse_arguments():
	arguments = {
		'data': './datat.txt',
	}
	try:
		opts, args = getopt.getopt(sys.argv[1:], 'hc:tn', ['data'])
	
	except getopt.GetoptError as err:
		print str(err)  # will print something like "option -a not recognized"
		usage()
		sys.exit(2)
	
	for o, a in opts:
		if o in ('-d', '--data'):
			arguments['data'] = a
		else:
			assert False, 'unknown option `' + o + '`' 

	return arguments

def main():
	arguments = parse_arguments()

	init()

	with open(arguments['data']) as data_file:
		global data
		data = json.load(data_file)

	pprint.pprint(data)

	data0 = map(lambda elem: elem[0], data["data"])
	data1 = map(lambda elem: elem[1], data["data"])
	data2 = map(lambda elem: elem[2], data["data"])
	data_data = [data0,data1,data2]

	plot_surface(data_data,"t")



def plot_surface(surface, f):
	fig = plt.figure()
	ax = fig.add_subplot(111, projection='3d')
	legends = ['Terreno Objetivo']
	ax.scatter(surface[0], surface[1], surface[2], c="y")
	plt.legend(legends)
	for angle in range(0, 181, 5):
		ax.view_init(30, angle)
		plt.savefig('terrain' + f + '-angle' + str(angle) + '.png')
	plt.close()


if __name__ == '__main__':
	main()