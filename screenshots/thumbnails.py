#!/usr/bin/env python

import os
from PIL import Image
from pprint import pprint

app_path = "MyIntent/myintent"

filenames = [f for f in os.listdir(".") if f[-4:] == ".png"]
filenames.sort()

for name in filenames:
    im = Image.open(name)
    size = 128, 128
    im.thumbnail(size, Image.ANTIALIAS)
    im.save(os.path.join("thumbnails", name))
    print "[![" + name + "](screenshots/thumbnails/" + name + ")](https://raw.githubusercontent.com/langara/" + app_path + "/screenshots/" + name + ")"

