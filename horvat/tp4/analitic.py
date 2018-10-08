from algorithm import Algorithm

class Analitic(Algorithm):
	def __init__(self, r, v, r_function, v_function, a_function):
		super(Analitic, self).__init__(r = r, v = v, a_function = a_function)
		self.a_function = a_function
		self.v_function = v_function
		self.r_function = r_function
		self.update(0)

	def update(self,t):
		self.r = self.r_function(t)
		self.v = self.v_function(t)
		self.a = self.a_function(r = self.r, v = self.v)
		
	
	def basic_loop(self, params):
		t = params[0]
		self.update(t)
		