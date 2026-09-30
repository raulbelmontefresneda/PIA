# -*- coding: utf-8 -*-
"""
Genera una figura tipo "tabla" como la de tu imagen:
- Aprendizaje supervisado: clasificación binaria, multiclase, regresión
- Aprendizaje no supervisado: clustering (no etiquetado vs etiquetado por clusters)
Requiere: numpy, matplotlib, scikit-learn
"""

import numpy as np
import matplotlib.pyplot as plt
from matplotlib import gridspec
from sklearn.datasets import make_classification, make_blobs
from sklearn.linear_model import LogisticRegression, LinearRegression
from sklearn.preprocessing import PolynomialFeatures
from sklearn.pipeline import make_pipeline
from sklearn.cluster import KMeans

np.random.seed(7)

# -----------------------------
# Datos: clasificación binaria
# -----------------------------
Xb, yb = make_classification(
    n_samples=140, n_features=2, n_redundant=0, n_informative=2,
    n_clusters_per_class=1, class_sep=1.2, random_state=2
)

clf_bin = LogisticRegression().fit(Xb, yb)

# Malla para regiones
xb0_min, xb0_max = Xb[:, 0].min() - 1, Xb[:, 0].max() + 1
xb1_min, xb1_max = Xb[:, 1].min() - 1, Xb[:, 1].max() + 1
xxb, yyb = np.meshgrid(np.linspace(xb0_min, xb0_max, 250),
                       np.linspace(xb1_min, xb1_max, 250))
Zb = clf_bin.predict(np.c_[xxb.ravel(), yyb.ravel()]).reshape(xxb.shape)

# -----------------------------
# Datos: clasificación multiclase
# -----------------------------
Xm, ym = make_blobs(n_samples=180, centers=3, cluster_std=1.0, random_state=4)
clf_multi = LogisticRegression(multi_class="auto").fit(Xm, ym)

xm0_min, xm0_max = Xm[:, 0].min() - 1, Xm[:, 0].max() + 1
xm1_min, xm1_max = Xm[:, 1].min() - 1, Xm[:, 1].max() + 1
xxm, yym = np.meshgrid(np.linspace(xm0_min, xm0_max, 250),
                       np.linspace(xm1_min, xm1_max, 250))
Zm = clf_multi.predict(np.c_[xxm.ravel(), yym.ravel()]).reshape(xxm.shape)

# -----------------------------
# Datos: regresión (1D)
# -----------------------------
n = 160
xr = np.linspace(50, 225, n)
yr = 60 - 0.35 * xr + 0.0012 * xr**2 + np.random.normal(0, 3.2, size=n)

reg_poly = make_pipeline(PolynomialFeatures(degree=2, include_bias=False),
                         LinearRegression())
reg_poly.fit(xr.reshape(-1, 1), yr)
xr_grid = np.linspace(xr.min(), xr.max(), 300)
yr_pred = reg_poly.predict(xr_grid.reshape(-1, 1))

# -----------------------------
# Datos: clustering (no supervisado)
# -----------------------------
Xc, _ = make_blobs(n_samples=250, centers=10, cluster_std=0.13, random_state=3)
km = KMeans(n_clusters=10, n_init=10, random_state=3).fit(Xc)
yc = km.labels_

# -----------------------------
# Layout tipo "tabla"
# -----------------------------
fig = plt.figure(figsize=(15, 8), dpi=160)
gs = gridspec.GridSpec(
    nrows=4, ncols=5, figure=fig,
    width_ratios=[1.2, 1.9, 1.7, 2.2, 2.2],   # columnas: ML | tipo | tarea | datos | modelo
    height_ratios=[1.0, 1.0, 1.2, 1.2],       # filas: binaria | multiclase | regresión | clustering
    wspace=0.25, hspace=0.55
)

def boxed_ax(r0, r1, c0, c1):
    ax = fig.add_subplot(gs[r0:r1, c0:c1])
    ax.set_xticks([]); ax.set_yticks([])
    for sp in ax.spines.values():
        sp.set_visible(True)
    ax.set_facecolor("white")
    return ax

# Col 0: "Aprendizaje automático" (toda la altura)
ax0 = boxed_ax(0, 4, 0, 1)
ax0.text(0.5, 0.5, "Aprendizaje\nautomático", ha="center", va="center",
         fontsize=15, fontweight="bold")

# Col 1: supervisado (filas 0-3) y no supervisado (fila 3)
ax1_sup = boxed_ax(0, 3, 1, 2)
ax1_sup.text(0.5, 0.5, "Aprendizaje\nsupervisado\n(Datos etiquetados)",
             ha="center", va="center", fontsize=13, fontweight="bold")

ax1_unsup = boxed_ax(3, 4, 1, 2)
ax1_unsup.text(0.5, 0.5, "Aprendizaje no\nsupervisado\n(Datos no etiquetados)",
               ha="center", va="center", fontsize=12, fontweight="bold")

# Col 2: tareas (clasificación arriba, regresión, clustering)
ax2_cls = boxed_ax(0, 2, 2, 3)
ax2_cls.text(0.5, 0.5, "Clasificación\n(Etiquetas categóricas)",
             ha="center", va="center", fontsize=13, fontweight="bold")

ax2_reg = boxed_ax(2, 3, 2, 3)
ax2_reg.text(0.5, 0.5, "Regresión\n(Etiquetas numéricas)",
             ha="center", va="center", fontsize=13, fontweight="bold")

ax2_clu = boxed_ax(3, 4, 2, 3)
ax2_clu.text(0.5, 0.5, "Clustering",
             ha="center", va="center", fontsize=13, fontweight="bold", style="italic")

# Col 3-4: filas con gráficos
def plot_scatter_labeled(ax, X, y, title):
    ax.set_title(title, fontsize=11, pad=6)
    ax.scatter(X[:, 0], X[:, 1], c=y, s=14, edgecolors="k", linewidths=0.2)
    ax.tick_params(labelsize=8)

def plot_regions(ax, xx, yy, Z, X, y, title):
    ax.set_title(title, fontsize=11, pad=6)
    ax.contourf(xx, yy, Z, alpha=0.45)
    ax.scatter(X[:, 0], X[:, 1], c=y, s=14, edgecolors="k", linewidths=0.2)
    ax.tick_params(labelsize=8)

def plot_reg(ax, x, y, title):
    ax.set_title(title, fontsize=11, pad=6)
    ax.scatter(x, y, s=10, edgecolors="k", linewidths=0.15)
    ax.tick_params(labelsize=8)

def plot_reg_model(ax, x, y, xg, yg, title):
    ax.set_title(title, fontsize=11, pad=6)
    ax.scatter(x, y, s=10, edgecolors="k", linewidths=0.15)
    ax.plot(xg, yg, linewidth=2.0)
    ax.tick_params(labelsize=8)

# Fila 0: binaria
ax_bin_lbl = fig.add_subplot(gs[0, 3])
plot_scatter_labeled(ax_bin_lbl, Xb, yb, "Datos etiquetados")

ax_bin_mod = fig.add_subplot(gs[0, 4])
plot_regions(ax_bin_mod, xxb, yyb, Zb, Xb, yb, "Modelo")

ax_mid0 = boxed_ax(0, 1, 3, 3)  # dummy (no usado)
plt.delaxes(ax_mid0)            # quitamos dummy

ax_txt_bin = boxed_ax(0, 1, 3, 3)  # no existe; hacemos columna 3 ya ocupada
plt.delaxes(ax_txt_bin)

ax_bin_type = boxed_ax(0, 1, 3, 3)

# En lugar de columna extra, ponemos el texto en col 3-4 superior izquierda mediante anotación:
# Creamos un pequeño eje en col 3 a la izquierda de los gráficos usando gridspec "inset"
ax_bin_label = boxed_ax(0, 1, 3, 3)  # placeholder, pero no podemos; se elimina

# Más simple: usar col 3 (datos) con título y añadir el texto en col 2 (clasificación) no cabe.
# Creamos un eje estrecho dentro de la celda col 2-3 para el subtipo:
ax_sub_bin = boxed_ax(0, 1, 3, 3)
plt.delaxes(ax_sub_bin)

ax_sub_bin = fig.add_subplot(gs[0, 3])  # reutilizamos el de datos para poner texto en su margen
ax_sub_bin.text(-0.55, 0.5, "Clasificación\nbinaria\n(Dos clases)",
                transform=ax_sub_bin.transAxes, ha="center", va="center",
                fontsize=11, fontweight="bold")
# Re-plot encima
ax_sub_bin.cla()
plot_scatter_labeled(ax_sub_bin, Xb, yb, "Datos etiquetados")
ax_sub_bin.text(-0.55, 0.5, "Clasificación\nbinaria\n(Dos clases)",
                transform=ax_sub_bin.transAxes, ha="center", va="center",
                fontsize=11, fontweight="bold")

# Fila 1: multiclase
ax_multi_lbl = fig.add_subplot(gs[1, 3])
plot_scatter_labeled(ax_multi_lbl, Xm, ym, "Datos etiquetados")
ax_multi_lbl.text(-0.55, 0.5, "Clasificación\nmulticlase\n(más de dos\nclases)",
                  transform=ax_multi_lbl.transAxes, ha="center", va="center",
                  fontsize=11, fontweight="bold")

ax_multi_mod = fig.add_subplot(gs[1, 4])
plot_regions(ax_multi_mod, xxm, yym, Zm, Xm, ym, "Modelo")

# Fila 2: regresión
ax_reg_lbl = fig.add_subplot(gs[2, 3])
plot_reg(ax_reg_lbl, xr, yr, "Datos etiquetados")

ax_reg_mod = fig.add_subplot(gs[2, 4])
plot_reg_model(ax_reg_mod, xr, yr, xr_grid, yr_pred, "Modelo")

# Fila 3: clustering
ax_clu_raw = fig.add_subplot(gs[3, 3])
ax_clu_raw.set_title("Datos no etiquetados", fontsize=11, pad=6)
ax_clu_raw.scatter(Xc[:, 0], Xc[:, 1], s=10, edgecolors="k", linewidths=0.15)
ax_clu_raw.tick_params(labelsize=8)

ax_clu_lab = fig.add_subplot(gs[3, 4])
ax_clu_lab.set_title("Datos etiquetados", fontsize=11, pad=6)
ax_clu_lab.scatter(Xc[:, 0], Xc[:, 1], c=yc, s=10, edgecolors="k", linewidths=0.15)
ax_clu_lab.tick_params(labelsize=8)

# Bordes "tipo tabla" para los ejes con plots (spines visibles)
for ax in [ax_bin_lbl, ax_bin_mod, ax_multi_lbl, ax_multi_mod, ax_reg_lbl, ax_reg_mod, ax_clu_raw, ax_clu_lab]:
    for sp in ax.spines.values():
        sp.set_visible(True)

# Guardado
out = "tabla_aprendizaje_automatico.png"
plt.savefig(out, bbox_inches="tight")
plt.show()

print(f"Guardado como: {out}")
