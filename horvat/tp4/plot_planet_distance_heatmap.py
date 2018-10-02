
import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np


def main():

    L_info, v_info, mapp = parse_file("./output/planet_distance_data.json")
    plot_map(L_info, v_info, mapp)

def parse_file(dir):
    with open(dir) as data:
        import json
        a = json.loads(data.readline())
        return (a[0],a[1],np.array(a[2]))


def plot_map(L_info, v_info, mapp):

    Lmin = L_info[0]
    Lmax = L_info[1]
    dL = L_info[2]
    vmin = v_info[0]
    vmax = v_info[1]
    dv = v_info[2]
    cc = np.arange(vmin, vmax, dv)

    ii = np.arange(Lmin, Lmax, dL)

    data = pd.DataFrame(mapp, columns=cc, index=ii)
    ax = sns.heatmap(data, cmap= "BuPu_r")
    for ww in ax.get_yticklabels():
        ww.set_rotation(0)
    plt.xlabel("v0 (km/s)")
    plt.ylabel("L (km)")
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
