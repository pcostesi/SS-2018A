data = dlmread("caudal.out", "'\t'");
t = data(:,1);
caudal = data(:,2);
plot(t, caudal, "Part/Seg");
title ("Evolucion Caoudal");
xlabel ("Tiempo (s)");
ylabel ("Caudal(Part/s)");
set(gca, "linewidth", 1, "fontsize", 12);