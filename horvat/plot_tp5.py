import matplotlib.pyplot as plt
import numpy as np

def plot_surface(ds,name):
    fig = plt.figure()
    ax = plt.gca()
    legends = ['D1','D2','D3','D4']
    i = 1
    for d in ds:
        if name == 'Q':
            x = d["tarr"][DT-1:]
            y = d["Qarr"] * i
        else:
            x = d["tarr"]
            y = d["Earr"] * i

        ax.errorbar(x, y, fmt='o')
        i +=1
    ax.set_yscale('log')

    plt.legend(legends)
    #plt.show()
    plt.savefig(name + '.png')
    plt.close()

DT = 10

def parse_file(filename):
    Qc = []
    Q = []
    E = []
    t = []
    with open(filename,"r") as file:
        for line in file.readlines():
            parts = line.split("\t")
            t.append(float(parts[0]))
            Qc.append(float(parts[1]))
            E.append(float(parts[2]))

    Earr = np.array(E)
    for i in xrange(DT,Earr.size+1):
       Q.append(sum(Qc[i-DT:i]))
    Qarr = np.array(Q)
    print(filename)
    print(" Q avg ")
    print(np.average(Qarr))
    print(" Q std ")
    print(np.std(Qarr))
    tarr = np.array(t)
    return {"tarr": tarr, "Qarr": Qarr, "Earr": Earr}



def mains():
    d1 = parse_file("./d1.out")
    d2 = parse_file("./d2.out")
    d3 = parse_file("./d3.out")
    d4 = parse_file("./d4.out")
    plot_surface([d1,d2,d3,d4], "Q")
    plot_surface([d1,d2,d3,d4], "E")

if __name__ == '__main__':
    mains()

