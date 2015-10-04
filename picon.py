#!/usr/bin/env python
"""Small tool for copying android icons from material-design-icons repo to specified android gradle module.

    It copies all density versions of png files to appropriate res subdirectories.

    Usage:
        picon.py add <category> <name> [-i <path>] [-o <path>] [-c <color>] [-s <size>]
        picon.py rem <name> [-o <path>] [-c <color>] [-s <size>]
        picon.py (-h | --help)
        picon.py (-v | --version)


    Options:
        -c, --color <color>     Which color version to use (black or white or all) [default: all]
        -s, --size <size>       Which size to use (number in dp units or 'all') [default: all]
        -i, --input <path>      Path where local copy of material-design-icons repo is located [default: /media/data/android_big/material-design-icons]
        -o, --output <path>     Path of top android module directory where icons will be copied [default: /home/marek/code/android/MyIntent/myres]
        -h, --help              Show help screen.
        -v, --version           Show version.

    Commands:
        add:                    copy new icon from material-design-icons repo to android module
        rem:                    remove all versions of given icon from android module

"""


VERSION='0.1.0'

try:
    from docopt import docopt
except ImportError:
    print 'This script needs a "docopt" module (http://docopt.org)'
    raise

from shutil import copyfile
from os import remove
from os import mkdir
from os.path import join
from os.path import isdir


densities = [
    "mdpi",
    "hdpi",
    "xhdpi",
    "xxhdpi",
    "xxxhdpi",
]

def add(category, name, color, size, inp, outp):

    if color == "all":
        add(category, name, "black", size, inp, outp)
        add(category, name, "white", size, inp, outp)
        return

    if size == "all":
        add(category, name, color, "18", inp, outp)
        add(category, name, color, "24", inp, outp)
        add(category, name, color, "36", inp, outp)
        add(category, name, color, "48", inp, outp)
        return

    name = name + "_" + color + "_" + size + "dp.png"
    for density in densities:
        idir = join(inp, category, "drawable-" + density)
        odir = join(outp, "src", "main", "res", "drawable-" + density)  
        if not isdir(odir):
            mkdir(odir)
        copyfile(join(idir, name), join(odir, name))


def rem(name, color, size, outp):

    if color == "all":
        rem(name, "black", size, outp)
        rem(name, "white", size, outp)
        return

    if size == "all":
        rem(name, color, "18", outp)
        rem(name, color, "24", outp)
        rem(name, color, "36", outp)
        rem(name, color, "48", outp)
        return

    name = name + "_" + color + "_" + size + "dp.png"
    for density in densities:
        ofile = join(outp, "src", "main", "res", "drawable-" + density, name)  
        try:    
            remove(ofile)
        except OSError:
            print "Can not remove:", ofile 



def main():

    argdict = docopt(__doc__, version=VERSION)

    if argdict["add"]:
        add(argdict["<category>"], argdict["<name>"], argdict["--color"], argdict["--size"], argdict["--input"], argdict["--output"])
    elif argdict["rem"]:
        rem(argdict["<name>"], argdict["--color"], argdict["--size"], argdict["--output"])



if __name__ == '__main__':
    main()

