#!/usr/bin/env python3
# -*- encoding: utf-8 -*-
import re

TIMEFRAME = re.compile(r'^\d+$')

def parse(filename):
    with open(filename) as source:
        lines = iter(source)
        current_timeframe = int(next(lines))
        particles = []
        for line in source.readlines():
            if TIMEFRAME.match(line):
                yield (current_timeframe, particles)
                current_timeframe = int(line)
                particles = []
            else:
                particles.append(tuple(map(float, line.split())))
        yield (current_timeframe, particles)


