cat("barev napastak\n")
library(ALTreports)
library(gridExtra)

args = commandArgs(trailingOnly = TRUE)
cat(length(args), " arguments supplied\n" )
parent.dir = args[1]
cat("parent dir is ", parent.dir, "\n")

iteration = args[2]
cat("Iteration is ", iteration, "\n")

pdf.file = args[3]
cat("pdf.file is ", pdf.file, "\n")


pdf(file = pdf.file)

gsub = T
#parent.dir = "C:/Dropbox/Bioinformatics_Group/The_telomere_project/telomere_network/alt-tert-networks/p9.cl.av"
alt.tert.dir = "alt-tert"
#iteration = "alt_1.27-tert_1.18"
samples.file = file.path(parent.dir, "samples.RData")
load(samples.file)
summary.file = file.path(parent.dir, alt.tert.dir, iteration, "alt-tert_summary.xls")
alt.target = "ALT"
tert.target = "telomere elongation"
net.dir = "alt-tert"

##########      process ALT results

alt.table = getPsfScores(summary.file, alt.target)
if(gsub){
  rownames(alt.table) = gsub(".", "-", rownames(alt.table), fixed = T)
}
alt.frame = gen.psf.frame(alt.table, samples, alt.target, 0.95)
pss = performance.scores(alt.frame, alt.target)
unlist(pss$PSS, use.names = T)
pss$KW
pss$KWPH

volcano.plot(alt.frame, alt.target, samples, columns = c("phen"))
boxandwhisker(alt.frame, alt.target, samples, columns = c("phen"))

##########      process tert results

tert.table = getPsfScores(summary.file, tert.target)
if(gsub){
  rownames(tert.table) = gsub(".", "-", rownames(tert.table), fixed = T)
}
tert.frame = gen.psf.frame(tert.table, samples, tert.target, 0.95)
pss = performance.scores(tert.frame, tert.target, "tert")
unlist(pss$PSS, use.names = T)
pss$KW
pss$KWPH

volcano.plot(tert.frame, tert.target, samples, "phen")
boxandwhisker(tert.frame, tert.target, samples, "phen")


##########      combined alt-tert separation plot
tert.frame = tert.frame[rownames(alt.frame),]
alt.tert.frame = cbind(alt = alt.frame[,alt.target], 
                       tert = tert.frame[,tert.target], 
                       phen = as.character(alt.frame[,"phen"]))
rownames(alt.tert.frame) = rownames(alt.frame)

phen.col = as.character(samples[rownames(alt.frame),"col"])
shape.code = c("cell line" = 19, "tissue"=17)
par(xpd = FALSE)
x = tert.frame[,tert.target]
y = alt.frame[,alt.target]
plot(bty='L', x, y, xlab = "telomerase", ylab = "ALT",
     col=phen.col, xlim = c(min(x), max(x)+0.3), cex = 1.5,
     pch = shape.code[as.character(samples[rownames(alt.frame), "category"])])
text(x+0.01, y+0.02, rownames(alt.frame), cex = 0.5)
par(xpd = TRUE)
legend(x = max(x)+0.1, y = max(y)-0.1, unique(samples[rownames(alt.frame),"phen"]), fill = unique(phen.col))

abline(1,0, col = "red")
abline(v = 1, col = "blue")

##########      svm
alt.tert.frame = as.data.frame(alt.tert.frame)
phen.train = alt.tert.frame[,]
phen.train[,1] = as.numeric(as.character(phen.train[,1]))
phen.train[,2] = as.numeric(as.character(phen.train[,2]))

xalt = phen.train[-which(phen.train$phen == "norm"),c("alt", "phen")]
xtert = phen.train[-which(phen.train$phen == "norm"),c("tert", "phen")]
library(e1071)
svm.model = svm(phen ~ ., data = xalt, kernel = "linear", cost = 5, scale = F)
pa.alt = as.character(predict(svm.model, xalt))
xalt$phen
w = t(svm.model$coefs) %*% svm.model$SV
b = svm.model$rho
h = b/w[1]


svm.model = svm(phen ~ ., data = xtert, kernel = "linear", cost = 1, scale = F)
pa.tert = as.character(predict(svm.model, xtert))
xtert$phen
w = t(svm.model$coefs) %*% svm.model$SV
b = svm.model$rho
v = b/w[1]


p.alt = which(phen.train$alt > h & phen.train$tert < v)
p.tert = which(phen.train$alt < h & phen.train$tert > v)
p.norm = which(phen.train$alt < h & phen.train$tert < v)
p.both = which(phen.train$alt > h & phen.train$tert > v)



phen.table = matrix(nrow = 4, ncol = 3)
rownames(phen.table) = c("alt", "tert", "norm", "both")
colnames(phen.table) = c("alt", "tert", "norm")

phen.table["alt", "alt"] = length(intersect(p.alt, which(phen.train$phen == "alt")))
phen.table["alt", "tert"] = length(intersect(p.alt, which(phen.train$phen == "tert")))
phen.table["alt", "norm"] = length(intersect(p.alt, which(phen.train$phen == "norm")))

phen.table["tert", "alt"] = length(intersect(p.tert, which(phen.train$phen == "alt")))
phen.table["tert", "tert"] = length(intersect(p.tert, which(phen.train$phen == "tert")))
phen.table["tert", "norm"] = length(intersect(p.tert, which(phen.train$phen == "norm")))

phen.table["norm", "alt"] = length(intersect(p.norm, which(phen.train$phen == "alt")))
phen.table["norm", "tert"] = length(intersect(p.norm, which(phen.train$phen == "tert")))
phen.table["norm", "norm"] = length(intersect(p.norm, which(phen.train$phen == "norm")))

phen.table["both", "alt"] = length(intersect(p.both, which(phen.train$phen == "alt")))
phen.table["both", "tert"] = length(intersect(p.both, which(phen.train$phen == "tert")))
phen.table["both", "norm"] = length(intersect(p.both, which(phen.train$phen == "norm")))

pred.accuracy = (phen.table[1,1] + phen.table[2,2] + phen.table[3,3])/sum(sum(phen.table))

data.xy  = phen.train[,2:1]
data.col = samples[rownames(data.xy), "col"]
title = paste0("Predication accuracy: ", round(pred.accuracy,2))
plot(data.xy, xlab = "Telomerase", ylab = "ALT",
     col=data.col, cex = 2,pch = 19, main = title)
points(data.xy[p.norm,],pch="/", col="white", cex=1)
points(data.xy[p.alt,],pch="#", col="white", cex=1)
points(data.xy[p.tert,],pch="+", col="white", cex=1)
points(data.xy[p.both,],pch="?", col="white", cex=1)
abline(h = h, col = "red")
abline(v = v, col = "blue")

##########      gene expression plots
nodes.csv = file.path(parent.dir, net.dir, iteration, paste0("nodes_", iteration, ".csv"))
nodes = read.csv(nodes.csv, header = T)

alt.tert.fc.file = file.path(parent.dir, alt.tert.dir, iteration,
                             paste0("fc_", iteration, ".txt"))
alt.tert.fc = read.table(file = alt.tert.fc.file, header = T, sep = "\t", row.names = 1, check.names = F)
alt.tert.fc = alt.tert.fc[as.character(nodes$name),]
alt.fc = alt.tert.fc[which(nodes$network == "alt"),]
tert.fc = alt.tert.fc[which(nodes$network == "tert"),]
alt.fc = alt.fc[-which(apply(alt.fc,1, sd)==0),]
tert.fc = tert.fc[-which(apply(tert.fc,1, sd)==0),]    

# phen.order = c("alt.cl", "alt.t", "norm.cl", "norm.t",  "tert.cl", "tert.t")


library(dplyr)
samples.df = as.data.frame(samples)
order = c(which(samples.df$phen == "alt" & samples.df$category == "cell line"),
          which(samples.df$phen == "alt" & samples.df$category == "tissue"),
          which(samples.df$phen == "norm" & samples.df$category == "cell line"),
          which(samples.df$phen == "norm" & samples.df$category == "tissue"),
          which(samples.df$phen == "tert" & samples.df$category == "cell line"),
          which(samples.df$phen == "tert" & samples.df$category == "tissue"),
          which(samples.df$phen == "nonalt" & samples.df$category == "cell line"),
          which(samples.df$phen == "nonalt" & samples.df$category == "tissue"))
samples.df = samples.df[order,]
if(gsub){
  colnames(tert.fc) = gsub(".", "-", colnames(tert.fc), fixed = T )
  colnames(alt.fc) = gsub(".", "-", colnames(alt.fc), fixed = T )
}
tert.fc = tert.fc[,rownames(samples.df)]
alt.fc = alt.fc[,rownames(samples.df)]

heatmap(as.matrix(alt.fc), Colv = NA,
        ColSideColors = as.character(samples.df[colnames(alt.fc),]$col),
        labCol =  as.character(samples.df[colnames(tert.fc),]$name),
        col = colorRampPalette(c("blue", "white", "red"))(n = 100),
        main = "ALT heatmap",
        margins = c(5,1))

heatmap(as.matrix(tert.fc), Colv = NA,
        ColSideColors = as.character(samples.df[colnames(tert.fc),]$col),
        col = colorRampPalette(c("blue", "white", "red"))(n = 100),
        main = "telomerase heatmap",
        labCol = as.character(samples.df[colnames(tert.fc),]$name),
        margins = c(5,1))





dev.off()