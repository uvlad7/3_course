require 'pycall/import'
include PyCall::Import
pyfrom('matplotlib', import: :pyplot)
pyfrom('mpl_toolkits.mplot3d', import: :axes3d)
pyimport('numpy', as: :np)

[5.0, 50.0].each do |n|
  m = 20.0*n**2 # τ = h^2/20
  exact = Array.new(m + 1) { Array.new(n + 1) }
  h = 1.0 / n
  th = 1.0 / m
  (0..m).each do |k|
    (0..n).each do |i|
      exact[k][i] = ((k*th)**2 - 2)**3 + (i*h)**3
    end
  end
  matrix = Array.new(m + 1) { Array.new(n + 1) }
  (0..n).each do |i|
    matrix[0][i] = (i*h)**3 - 8
  end
  (1..m).each do |k|
    matrix[k][0] =  ((k*th)**2 - 2)**3
  end
  t1 = Time.now.to_i
  delta = 0.0
  (0...m).each do |k|
    t = k*th
    (1...n).each do |i|
      x = i*h
      matrix[k + 1][i] = (matrix[k][i + 1] - 2.0*matrix[k][i] + matrix[k][i - 1])/4.0 +
          th*(6*t*(t**2 - 2.0)**2 - 30.0*x) + matrix[k][i]
      delta = [(matrix[k + 1][i] - exact[k + 1][i]).abs, delta].max
    end
    matrix[k + 1][n] = (10.0*th*matrix[k + 1][n - 1] + 30.0*th*h + h**2*matrix[k][n] +
        h**2*th*(6*(t + th)*((t + th)**2 - 2)**2 - 30))/(h**2 * 1.5)
    delta = [(matrix[k + 1][n] - exact[k + 1][n]).abs, delta].max
  end
  t2 = Time.now.to_i - t1
  puts "N = #{n}, M = #{m}, delta = #{delta}, time = #{t2} seconds"
  xs, ys = np.meshgrid(np.arange(0, 1 + h, h), np.arange(0, 1 + th, th))
  fig = pyplot.figure
  ax = fig.add_subplot(111, projection: '3d')
  ax.plot_surface(xs, ys, np.array(exact), color: 'green')
  ax.plot_wireframe(xs, ys, np.array(matrix), color: 'red')
  ax.set_xlabel('X')
  ax.set_ylabel('T')
  ax.set_zlabel('Y')
  pyplot.show
end
