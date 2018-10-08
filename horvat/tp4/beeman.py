from algorithm import Algorithm

class Beeman(Algorithm):
	"""docstring for Beeman"""
	def __init__(self, r, v, a_function, dt):
		super(Beeman, self).__init__(r = r, v = v, a_function = a_function)
		self.a_prev = a_function(r = r - v * dt, v = v - self.a * dt)
		self.dt = dt

	def update_r(self):
		r = self.r
		v = self.v
		a = self.a
		dt = self.dt
		a_prev = self.a_prev
		self.r = r + v * dt + 2/3.0* a * dt**2 - 1/6.0 * a_prev * dt**2

	def update_v_predicted(self):
		v = self.v
		a = self.a
		a_prev = self.a_prev
		dt = self.dt
		self.v_pred = v + 3/2.0*a*dt - 1/2.0*a_prev * dt**2

	def get_a_next(self):
		self.a_next = self.a_function(r = self.r, v = self.v_pred)

	def update_v_corrected(self):
		r = self.r
		v = self.v
		a = self.a
		a_prev = self.a_prev
		a_next = self.a_next
		dt = self.dt
		self.v = v + 1/3.0 * a_next * dt + 5/6.0 * a * dt - 1/6.0 * a_prev * dt
		
	def basic_loop(self):
		#import ipdb ;ipdb.set_trace()
		self.update_r()
		self.update_v_predicted()
		self.get_a_next()
		self.update_v_corrected()
		self.a_prev = self.a
		self.a = self.a_next