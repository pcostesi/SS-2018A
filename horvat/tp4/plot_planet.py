
import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np


def main():

    xx = parse_file("./output/bbb.out")
    plot_line(xx)

def parse_file(dir):
    with open(dir) as data:
        a = []
        data_abail = data.readline()
        while data_abail:
            a.append([int(data_abail.split()[0]), float(data_abail.split()[1])])
            data_abail = data.readline()

        #return a

        return [map(lambda e: e[0], sorted(a, key=lambda elem: elem[0])),map(lambda e: e[1], sorted(a, key=lambda elem: elem[0]))]


def plot_line(line):

    # Lmin = L_info[0]
    # Lmax = L_info[1]
    # dL = L_info[2]
    # vmin = v_info[0]
    # vmax = v_info[1]
    # dv = v_info[2]
    #cc = np.arange(vmin, vmax, dv)

    #ii = np.arange(Lmin, Lmax, dL)

    #data = pd.DataFrame(mapp, columns=cc, index=ii)
    #ax = sns.heatmap(data, cmap= "BuPu_r")
    #for ww in ax.get_yticklabels():
    #    ww.set_rotation(0)
    data = np.array(line[1])/10000
    #x = np.arange(0,data.size)*100
    x = line[0]

    fig, ax = plt.subplots()
    legends = ['Densidad [part/m2]']
    ax.scatter(x, data)

    plt.xlabel("Angulo (grados)")
    plt.ylabel("Distancia (10$^4$ km)")
    plt.savefig('output/heatmap.png')


    # fig, ax = plt.subplots()
    # legends = ['Densidad [part/m2]']
    # ax.scatter(x, y)
    # ax.errorbar(x, y, yerr=yerr, fmt='o')
    # ax.scatter(x, y + 0.1, c="y")
    # ax.errorbar(x, y + 0.1, yerr=yerr2, fmt='o', c="y")
    #
    # plt.legend(legends)
    # plt.show()
    # plt.savefig('terrain'+'.png')
    # plt.close()


if __name__ == '__main__':
    main()
