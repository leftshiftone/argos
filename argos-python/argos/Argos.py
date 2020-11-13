from pathlib import Path
from subprocess import *


def jarWrapper(*args):
    process = Popen(['java', '-jar'] + list(args), stdout=PIPE, stderr=PIPE)
    ret = []
    while process.poll() is None:
        line = process.stdout.readline()
        if line != '' and line.endswith(b'\n'):
            ret.append(line[:-1])
    stdout, stderr = process.communicate()
    ret += stdout.split(b'\n')
    if stderr != '':
        ret += stderr.split(b'\n')
    ret.remove(b'')
    return ret


jar_path = Path("resources/argos-java.jar")
args = [jar_path]  # Any number of args to be passed to the jar file

# # args -> XML
args.append(Path("tests/resources/intentAssertionTest.xml"))

# # args -> DSL
# args.append(Path("tests/resources/assertionDSLTest.kts"))

print(args, "\n")

result = jarWrapper(*args)

for out in result:
    out = out.decode()
    print(out)
