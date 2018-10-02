
import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np


def main():

    xx = parse_file("./output/velocity-3.1-fine.out")
    plot_line(xx)

def parse_file(dir):
    with open(dir) as data:
        a = []
        data_abail = data.readline()
        while data_abail:
            a.append(float(data_abail.split()[1]))
            data_abail = data.readline()

        return a


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
    data = np.array(line)*3333
    x = np.arange(0,data.size)*100

    fig, ax = plt.subplots()
    legends = ['Densidad [part/m2]']
    ax.plot(x, data)

    plt.xlabel("t (s)")
    plt.ylabel("|v| (km/s)")
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
