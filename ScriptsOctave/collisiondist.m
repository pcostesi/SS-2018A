data2 = load('../collisionIntervals150');
dataMean = mean(data2);
intervals = 2*max(data2)/dataMean;
hist(data2(:,1),intervals);%,"facecolor", "m"
title ("Promedio de tiempo entre colisiones - N 150, T 300, S 5", 'fontsize',18);
xlabel ("Tiempo entre colisiones(s)", 'fontsize',16);
ylabel ("Ocurrencias", 'fontsize',16);
set(gca,'fontsize',18);
