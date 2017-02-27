library(ALTreports)
cat("barev qaq ashxarh volume 2\n")
args = commandArgs(trailingOnly = TRUE)

cat(length(args), " arguments supplied\n" )
parent.dir = args[1]

cat("parent dir is ", parent.dir, "\n")
raw.mat.file = file.path(parent.dir, "raw.mat.RData")
samples.file = file.path(parent.dir, "samples.RData")
load(samples.file)

if(file.exists(raw.mat.file)){
  load(raw.mat.file)
} else {
  series.file = file.path(parent.dir, "entrez_exp.txt")  
  raw.mat = read.raw(series.file = series.file) 
  save(raw.mat, file = raw.mat.file)
}


net.dir = "alt-tert"
iteration = args[2]
cat("iteration is ", iteration, "\n")
nodes.csv = file.path(parent.dir, net.dir, iteration, paste0("nodes_", iteration, ".csv"))
cat("reading nodes table: ", nodes.csv, "\n")
node.table = read.csv(nodes.csv, header = T)
head(node.table)
#normalization test
# fc.mat = (raw.mat - rowMeans(raw.mat))/as.vector(apply(raw.mat, 1, sd))
fc.mat = raw.mat/rowMeans(raw.mat)
fc.mat.nodes = matrix(1, nrow = nrow(node.table), ncol = ncol(fc.mat))

rownames(fc.mat.nodes) = node.table$name
colnames(fc.mat.nodes) = colnames(fc.mat)
if(sum(rownames(fc.mat) == "0") > 0){
  fc.mat = fc.mat[-which(rownames(fc.mat) == "0"),]
}
bb = as.character(node.table$entrez) %in% rownames(fc.mat)

ne = cbind(as.character(droplevels(node.table[bb,"name"])), as.character(node.table[bb,]$entrez))
fc.mat.nodes[ne[,1],] = fc.mat[ne[,2],]


na.ind = which(is.na(fc.mat.nodes))
if(length(na.ind) > 0)
  fc.mat.nodes[na.ind] = 1
zero.ind = which(fc.mat.nodes == 0)
if(length(zero.ind) > 0)
  fc.mat.nodes[zero.ind,] = 1

out.file =file.path(parent.dir, net.dir, iteration, paste0("fc_", iteration, ".txt"))
write.table(fc.mat.nodes, file = out.file, quote = F, sep = "\t", row.names = T, col.names = NA)
