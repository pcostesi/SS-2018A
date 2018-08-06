
class Algorithm(object):
	
	def __init__(self, r, v, a_function):
		self.a_function = a_function
		self.r = r
		self.v = v
		self.a = a_function(r = r, v = v)
		self.r_history = []#[r]
		self.v_history = []#[v]
		self.a_history = []#[self.a]

	def loop(self, *params):
		self.basic_loop(params) if len(params) > 0 else self.basic_loop()
		self.r_history.append(self.r)
		self.v_history.append(self.v)
		self.a_history.append(self.a)

	def basic_loop(self):
		raise "Not implemented method"

	def get_error(self,correct_history):
		if len(correct_history) != len (self.r_history):
			raise "Different length"

		return sum(map(lambda (index,elem): (elem - correct_history[index])**2,enumerate(self.r_history))) / len(correct_history)
