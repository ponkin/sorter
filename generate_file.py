#!/usr/bin/env python

import os
import sys
import argparse
from string import ascii_lowercase

from random import choice, randrange

def generate(lines, file=open("output.txt","w")):
    
    for _ in range(0, lines):
        word_count = randrange(25, 150)
        line = ""
        for _ in range(0, word_count):
            word_len = randrange(8, 101)
            line += " " + "".join(choice(ascii_lowercase) for i in range(word_len))
        file.write(line)
        file.write("\n")

    file.close()

def main(arguments):

    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('lines', help="Number of lines", type=int)
    parser.add_argument('-o', '--outfile', help="Output file",
                        default=sys.stdout, type=argparse.FileType('w'))

    args = parser.parse_args(arguments)

    generate(args.lines, args.outfile)

if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
