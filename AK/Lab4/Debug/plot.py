import numpy as np
from matplotlib import pyplot
from matplotlib import cm
from mpl_toolkits.mplot3d import Axes3D
for i in range(1, 5):
    u = np.loadtxt('output' + str(i) + '.txt', delimiter=' ')
    h = 1.0 / 250
    t = 1.0 / 500
    x, y = np.meshgrid(np.arange(0, 1 + h, h), np.arange(0, 1 + t, t))
    fig = pyplot.figure()
    ax = fig.add_subplot(111, projection='3d')
    ax.plot_surface(x, y, u, cmap=cm.tab20)
    ax.set_xlabel('x')
    ax.set_ylabel('y')
    ax.set_zlabel('u')
    pyplot.show()