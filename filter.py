import adblockparser, sys
from adblockparser import AdblockRules
f = open('easylist.txt', 'r')
all = f.read().splitlines()
rules = AdblockRules(all)
while True:
	# sys.stdin.open()
	s = sys.stdin.readline()

# print sys.argv[1]
	print rules.should_block(s)
	# sys.stdin.close()
	sys.stdout.close()
	sys.stdout = open('/dev/stdout', 'w')
	# open(sys.stdout)
	# open(sys.stdin)
	# print "^D"
