from __future__ import print_function
from time import sleep
import obd

connection = obd.Async()

connection.watch(obd.commands.RPM)
connection.watch(obd.commands.ENGINE_LOAD)
connection.watch(obd.commands.THROTTLE_POS)
connection.watch(obd.commands.INTAKE_TEMP)
connection.watch(obd.commands.COOLANT_TEMP)

connection.start()

running = True

while(running):
	rpm = connection.query(obd.commands.RPM)
	load = connection.query(obd.commands.ENGINE_LOAD)
	throttle = connection.query(obd.commands.THROTTLE_POS)
	intake = connection.query(obd.commands.INTAKE_TEMP)
	coolant = connection.query(obd.commands.COOLANT_TEMP)
	if not rpm.is_null():
		print(rpm.value.magnitude, load.value.magnitude, throttle.value.magnitude, intake.value.magnitude, coolant.value.magnitude, sep=",")
	sleep(0.01)
