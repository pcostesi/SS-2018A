import json
import ipdb
import pprint
import colored_traceback
import sys
import os
import numpy
from math import floor
from math import cos
from math import sin
from math import tan
from math import atan2
from math import pi
from math import sqrt
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import getopt
import random
import time
import copy

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
	print('usage:	python main.py [options]')
	print('-h --help:	print this screen')
	print('-c --config=:	configuration file')
	print('-p --partition=:	Va value file will be generated for each iterations')
	print('-e --eta_step=:	Va value file will be generated for each iterations')
	print('-d --density_step=:	Va value file will be generated for each iterations')

def parse_arguments():
	arguments = {
		'config': './config/config.json',
	}
	try:
		opts, args = getopt.getopt(sys.argv[1:], 'hc:tn', ['help', 'config=', 'partition=', 'eta_step=', 'density_step='])
	
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
		elif o in ('-p','--partition'):
			arguments['partition'] = int(a)
		elif o in ('--eta_step'):
			arguments['eta_step'] = int(a)
		elif o in ('--density_step'):
			arguments['density_step'] = int(a)
		else:
			assert False, 'unknown option `' + o + '`' 

	return arguments

def get_neighbours(field, particle, rc, M, L):
	possible_neighbours = []
	x = particle["x"]
	y = particle["y"]
	x_c = int(floor(x * M / L))
	y_c = int(floor(y * M / L))
	xm1 = (x_c - 1) % M
	xp1 = (x_c + 1) % M
	ym1 = (y_c - 1) % M
	yp1 = (y_c + 1) % M
	x2py2 = x ** 2 + y ** 2

	x_a = [(x_c - i) % M for i in [-1,0,1]]
	y_a = [(y_c - i) % M for i in [-1,0,1]]
	for xx in x_a:
		for yy in y_a:
			possible_neighbours.extend(field[xx][yy])  
	#possible_neighbours.extend(field[xm1][y_c]) 
	#possible_neighbours.extend(field[xm1][yp1]) 
	#possible_neighbours.extend(field[x_c][ym1]) if border_control or ym1 >= 0  else noop()
	#possible_neighbours.extend(field[x_c][y_c]) 
	#possible_neighbours.extend(field[x_c][yp1]) if border_control or ym1 < M  else noop()
	#possible_neighbours.extend(field[xp1][ym1]) if border_control or (xp1 < M and ym1 >= 0) else noop()
	#possible_neighbours.extend(field[xp1][y_c]) if border_control or xp1 < M  else noop()
	#possible_neighbours.extend(field[xp1][yp1]) if border_control or (xp1 < M and yp1 < M) else noop()

	neighbours = filter(lambda part: ( 
		(L - abs(part["x"]-x))**2+(L-abs(part["y"]-y))**2 < (rc)**2
		or 
		(part["x"]-x)**2+(L-abs(part["y"]-y))**2 < (rc)**2
		or 
		(L - abs(part["x"]-x))**2+(part["y"]-y)**2 < (rc)**2
		or
		(part["x"]-x)**2+(part["y"]-y)**2 < (rc)**2
		), possible_neighbours)

	return neighbours

def get_field(M, L, in_particles = []):
	
	field = {} 
	for x in xrange(0, M):
		field_x = {} 
		for y in xrange(0, M):
			field_x[y]= []
		field[x] = field_x

	particles = []

	for index in xrange(0, len(in_particles)):
		x = in_particles[index]["x"]
		y = in_particles[index]["y"]
		vx = in_particles[index]["vx"]
		vy = in_particles[index]["vy"]
		r = in_particles[index]["r"]
		field[int(floor(x * M / L))][int(floor(y * M / L))].append({"part": index, "x": x, "y": y, "r": r, "vx": vx, "vy": vy, "x_c": int(floor(x * M / L)), "y_c": int(floor(y * M / L))})

	return field

def generate(N, L, r, v):
	particles = []
	for part in xrange(1, N + 1):
		x = random.random() * (L)
		y = random.random() * (L)
		angle = random.random() * 2 * pi
		vx = cos(angle) * v
		vy = sin(angle) * v
		angle = angle * 360 / 2 / pi
		#color = get_color(angle)
		particles.append({ "part": part, "x": x, "y": y, "vx": vx, "vy": vy, "r": r, "angle":angle})
	return particles

def get_info(particles, i, L):
	string = ""
	string += '\t' + str(len(particles)+4) + '\n'
	string += '\t' + str(i) + '\n'
	for particle in particles:
		string += '\t' + str(particle["x"]) + '\t' + str(particle["y"]) + '\t' + str(particle["r"]) + '\t' + str(particle["angle"]%360) + '\n'
	string += '\t' + str(0) + '\t' + str(0) + '\t' + str(0.00000001) + '\t' + str(0) + '\n'
	string += '\t' + str(L) + '\t' + str(0) + '\t' + str(0.00000001) + '\t' + str(0) + '\n'
	string += '\t' + str(0) + '\t' + str(L) + '\t' + str(0.00000001) + '\t' + str(0) + '\n'
	string += '\t' + str(L) + '\t' + str(L) + '\t' + str(0.00000001) + '\t' + str(0) + '\n'
	return string

VA = "va"
FILE = "file"

def save_file(typee,parameter,parameter_value,file_string):
	if typee == VA:
		with open('output/va_'+ str(parameter) +'=' +str(parameter_value) +'.txt', 'w') as outfile:
			outfile.write(file_string)
	elif typee == FILE:
		with open('output/neighbours_'+ str(parameter) +'=' +str(parameter_value)  +'.txt', 'w') as outfile:
			outfile.write(file_string)

def start(M , L, particles, rc, eta, partition, v, iterations, parameter):
	i = 0

	file_string = ""
	va_string = ""
	while i < iterations:
		print i
		field = get_field(M = M, L = L, in_particles = particles)

		neighbours = map(lambda (index, particle): (index, get_neighbours(field = field, particle = particle, rc = rc, M = M, L = L)), enumerate(particles))
		# HERE "part" no available, get enum ^


		file_string += get_info(particles,i,L)

		x_sum = 0
		y_sum = 0
		vx_sum = 0
		vy_sum = 0

		for (index,particle) in enumerate(particles):
			particle["x"]= (particle["x"] + particle["vx"] ) % L # dt = 1
			particle["y"]= (particle["y"] + particle["vy"] ) % L
			x_sum += particle["x"]
			y_sum += particle["y"]
			particle_neighbours = neighbours[index][1]
			sum_speed = reduce(lambda speed, neighbour: (speed[0] + neighbour["vy"], speed[1] + neighbour["vx"]), particle_neighbours, (0,0))
			rand = numpy.random.uniform()*eta-eta/2.0
			angle = (rand +
					atan2(sum_speed[0]/(v*len(particle_neighbours)),sum_speed[1]/(v*len(particle_neighbours))) )
			particle["angle"] = angle * 360 / (2 * pi)
			particle["vx"]= cos(angle) * v
			particle["vy"]= sin(angle) * v
			vx_sum += cos(angle) 
			vy_sum += sin(angle)
			# if index == 1:
			# 	print sum_speed
			# 	print rand
			# 	print atan2(sum_speed[0]/(v*len(particle_neighbours)),sum_speed[1]/(v*len(particle_neighbours))) %(2 * pi)
			# 	print particle["angle"] %360
			# 	import ipdb
			# 	ipdb.set_trace()
			# #particle["color"]= get_color(angle)

		if partition > 0 and i%partition == 0:
			va = sqrt(vx_sum**2 + vy_sum**2) / (v*len(particles))
			va_string += str(va).replace(".",",") + '\n'
		i+=1

		#import ipdb
		#ipdb.set_trace()


	file_string += get_info(particles,i,L)
	save_file(typee = FILE, parameter = parameter, parameter_value = len(particles),file_string = file_string)
	save_file(typee = VA, parameter = parameter, parameter_value = len(particles),file_string = va_string) if va_string != "" else (lambda x: x ,[])


def main():

	arguments = parse_arguments()

	partition = arguments['partition'] if 'partition' in arguments else 0
	eta_step = arguments['eta_step'] if 'eta_step' in arguments else 0
	density_step = arguments['density_step'] if 'density_step' in arguments else 0
	
	init()

	with open(arguments['config']) as data_file:
		global data
		data = json.load(data_file)

	pprint.pprint(data)

	M = data['world']["M"] if 'world' in data else data["M"] 
	L = data['world']["L"] if 'world' in data else data["L"]
	rc = data['world']["rc"] if 'world' in data else data["rc"]
	iterations = data['world']["interations"] if 'world' in data else data["iterations"]
	r = data['world']["r"] if 'world' in data else data["r"]
	v = data['world']["v"] if 'world' in data else data["v"]
	N = data['world']["N"] if 'world' in data else data["N"]
	eta = data['world']["eta"] if 'world' in data else data["eta"]
	particles = data['particles'] if 'particles' in data else generate(N = N, L = L, r = r, v = v)


	density = N/(L**2)

	if eta_step > 0:
		for eta_summ in numpy.arange(0, eta+eta*eta_step/100, eta*eta_step/100.0):
			start(M = M, L = L, rc = rc, particles = copy.deepcopy(particles), eta = eta_summ, partition = partition, v = v, iterations = iterations, parameter="particle_number")
	elif density_step > 0:
		for density_summ in xrange(density_step,100+density_step, density_step):
			N_step = int(floor(N * density_summ/100.0))
			particles = generate(N = N_step, L = L, r = r, v = v)
			start(M = M, L = L, rc = rc, particles = particles, eta = eta, partition = partition, v = v, iterations = iterations, parameter="particle_number")
	else:
			start(M = M, L = L, rc = rc, particles = particles, eta = eta, partition = partition, v = v, iterations = iterations, parameter="particle_number")



if __name__ == '__main__':
	main()