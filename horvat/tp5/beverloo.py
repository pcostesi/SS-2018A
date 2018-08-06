import matplotlib.pyplot as plt
import numpy as np
import getopt
import sys
import os
import json
import pprint
from mpl_toolkits.mplot3d import Axes3D
import math

def init():
	pass

def parse_arguments():
	arguments = {
		'data': './config/config_beverloo.json',
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
		data = json.load(data_file)

	Qob = data["Q"]
	N = data["N"]
	L = data["L"]
	h = data["h"]
	r = data["d"]
	d = data["D"]
	g = data["g"]

	np = N / (L * h) 
	print np
	B = np * math.sqrt(g)
	print B
	Q = []
	global cmax
	cmax = int(min(d)/r/0.01)
	for i in xrange(0,len(Qob)):
		Qi = []
		Qobi = Qob[i]
		for x in xrange(1,cmax):
			c = x * 0.01
	 		Qi.append(abs(Qobi - B * math.sqrt((d[i] - c*r) ** 3)))
	 	Q.append(Qi)

	legends = ['D = 0.15','D = 0.20','D = 0.25','D = 0.30', 'Total']
	labels = {"x": "c [s]","y": "Error"}
	plot(Q,legends,labels)

def plot(data,legends,labels):
	for some_data in data:
		plt.plot(map(lambda e: e*0.01, xrange(1,cmax)), some_data)
	QQ = []
	for i in xrange(1,cmax):
		Q = 0.0
		for some_data in data:
			Q += some_data[i-1]
		QQ.append(Q)
	plt.plot(map(lambda e: e*0.01, xrange(1,cmax)), QQ)
	print ((QQ.index(min (QQ)) + 1) * 0.01)
	plt.legend(legends)
	plt.xlabel(labels["x"])
	plt.ylabel(labels["y"])
	#plt.yscale('log')
	#_max = 1.0
	#_min = -1.0
	#_delta = (_max - _min) / 15
	#_delta = 0.01 if _delta == 0 else _delta
	#plt.yticks(np.arange(_min - _delta, _max + _delta, _delta))
	plt.savefig(os.path.join(os.getcwd(),"./i2.png"))
	plt.close()


if __name__ == '__main__':
	main()