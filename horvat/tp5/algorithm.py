
class Algorithm(object):
	
	def __init__(self):
		pass

	def loop(self,t):
		self.basic_loop(t)
		
	def get_info(self, i, L, W, D):
		string = ""
		string += '\t' + str(len(self.particles)+4) + '\n'
		string += '\t' + str(i) + '\n'
		
		for particle in self.particles:
			string += '\t' + str(particle.id) + '\t' + str(particle.rad) + '\t' + str(particle.r_corr[0]["x"]) + '\t' + str(particle.r_corr[0]["y"]) + '\t' + str(particle.r_corr[1]["x"]) + '\t' + str(particle.r_corr[1]["y"]) + '\t' + str(particle.r_corr[2]["x"]) + '\t' + str(particle.r_corr[2]["y"]) + '\t' + str(1) + '\t' + str(0) + '\t' + str(0) + '\n'
		string += '\t' + str(len(self.particles)) + '\t' + str(0.000000001) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\n'
		string += '\t' + str(len(self.particles)+1) + '\t' + str(0.000000001) + '\t' + str(W) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\n'
		string += '\t' + str(len(self.particles)+2) + '\t' + str(0.000000001) + '\t' + str(0) + '\t' + str(L) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\n'
		string += '\t' + str(len(self.particles)+3) + '\t' + str(0.000000001) + '\t' + str(W) + '\t' + str(L) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\t' + str(0) + '\n'
		#ipdb.set_trace()
		return string

	def basic_loop(self,t):
		raise "Not implemented method"

	def get_error(self,correct_history):
		if len(correct_history) != len (self.r_history):
			raise "Different length"

		return sum(map(lambda (index,elem): (elem - correct_history[index])**2,enumerate(self.r_history))) / len(correct_history)
