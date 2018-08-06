from math import factorial

from algorithm import Algorithm

class Gear5(Algorithm):
	"""docstring for Gear"""
	def __init__(self, r, v, a_function, dt, k,m):
		super(Gear5, self).__init__(r = r, v = v, a_function = a_function)
		self.dt = dt
		self.a_function = a_function	
		self.r_corr = self.get_initial_r_corr(r = r, v = v, k = k, m = m)
		self.r_pred = [0 for i in xrange(1,7)]
		self.alpha = [3/16.0,251/360.0,1.0,11/18.0,1/6.0,1/60.0]

	def get_initial_r_corr(self,r,v,k,m):
		next_r = r+v*self.dt+ self.a_function(r=r,v=v)/2.0*self.dt**2
		next_v = v+self.a_function(r=r,v=v)*self.dt
		dr = next_r - r
		k_div_m = k/m
		return [next_r,
				next_v,
				- k_div_m * dr, 
				- k_div_m * v, 
				k_div_m ** 2 * dr, 
				k_div_m ** 2 * v]

	def update_predicted(self):
		r = self.r_corr
		dt = self.dt
		self.r_pred[0] = r[0] + \
						 r[1] * dt + \
						 r[2] * dt ** 2 / factorial(2) + \
						 r[3] * dt ** 3 / factorial(3) + \
						 r[4] * dt ** 4 / factorial(4) + \
						 r[5] * dt ** 5 / factorial(5)
		self.r_pred[1] = r[1] + \
						 r[2] * dt + \
						 r[3] * dt ** 2 / factorial(2) + \
						 r[4] * dt ** 3 / factorial(3) + \
						 r[5] * dt ** 4 / factorial(4)
		self.r_pred[2] = r[2] + \
						 r[3] * dt + \
						 r[4] * dt ** 2 / factorial(2) + \
						 r[5] * dt ** 3 / factorial(3)
		self.r_pred[3] = r[3] + \
						 r[4] * dt + \
						 r[5] * dt ** 2 / factorial(2)
		self.r_pred[4] = r[4] + \
						 r[5] * dt
		self.r_pred[5] = r[5]

	def basic_loop(self):
		#import ipdb; ipdb.set_trace()
		self.update_predicted()
		self.r_corr[2] = self.a_function(r = self.r_pred[0], v = self.r_pred[1])
		a_diff = self.r_corr[2] - self.r_pred[2]
		dt = self.dt
		r2_diff = a_diff * dt ** 2 / factorial(2)
		for x in xrange(0,6):
		 	self.r_corr[x] = self.r_pred[x] + self.alpha[x] * r2_diff * factorial(x) / (dt**x)
		self.r = self.r_corr[0]
		self.v = self.r_corr[1]
		self.a = self.r_corr[2]
