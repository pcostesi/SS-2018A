from algorithm import Algorithm

class Verlet(Algorithm):
	def __init__(self, r, v, a_function, dt):
		super(Verlet, self).__init__(r = r, v = v, a_function = a_function)
		self.a_prev = a_function(r = r - v * dt, v = v - self.a * dt)
		self.dt = dt

	#MUST BE r[t+dt](r[t],v[t],a[t])
	def update_r(self):
		r = self.r
		v = self.v
		a = self.a
		dt = self.dt
		self.r = r + v * dt + dt**2 * a

	#MUST BE v[t+dt/2](v[t],a[t])
	#MUST BE v[t+dt](v[t+dt/2],a[t+dt])
	def update_v(self):
		v = self.v
		a = self.a
		dt = self.dt
		self.v = v + a*(dt/2)

	#MUST BE a[t+dt](r[t+dt], r[t+dt])
	def update_a(self):
		r = self.r
		v = self.v
		self.a = self.a_function(r = r, v = v)
	
	def basic_loop(self):

		self.update_r()
		self.update_v()
		self.update_a()
		self.update_v()
		