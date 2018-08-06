from ovito.io import *
import ovito
import os

path = "/Users/martin/Sites/simulacion-sistemas/tp2/output/neighbours_config/"

for file in os.listdir(path):
	node = import_file(path + file, columns = ["Position.X", "Position.Y", "Velocity.X", "Velocity.Y", "Radius", "Color.R", "Color.G", "Color.B"], multiple_frames = True)

	node.add_to_scene()

	vp = ovito.dataset.viewports.active_vp

	settings = ovito.vis.RenderSettings(
		filename = file + '.avi',
		size = (640, 480),
		range = ovito.vis.RenderSettings.Range.ANIMATION
	)

	vp.render(settings)


