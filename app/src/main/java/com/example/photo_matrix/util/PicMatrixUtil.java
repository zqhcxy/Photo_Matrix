package com.example.photo_matrix.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.example.photo_matrix.softglowfilter.BrightContrastFilter;
import com.example.photo_matrix.softglowfilter.GaussianBlurFilter;
import com.example.photo_matrix.softglowfilter.ImageData;

/**
 * 图片编辑的工具类：可实现图片的-怀旧效果、模糊效果、锐化效果、浮雕效果、底片效果、光照效果、翻转效果、素描效果等
 * 
 * @author zqh-pc
 * 
 */
public class PicMatrixUtil {

	/**
	 * 怀旧效果
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap oldRemeber(Bitmap bmp) {
		if (ImageCache.get("oldRemeber") != null) {
			return ImageCache.get("oldRemeber");
		}
		// 速度测试
		long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				int newColor = Color.argb(255, newR > 255 ? 255 : newR,
						newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
				pixels[width * i + k] = newColor;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("may", "used time=" + (end - start));
		ImageCache.put("oldRemeber", bitmap);
		return bitmap;
	}

	/**
	 * 柔化效果(高斯模糊)(优化后比上面快三倍)
	 * <p>
	 * 1、pixels数组不能超过规定的大小，也就是说图片的尺寸不能太大，否则会栈内存溢出。
	 * <p>
	 * 2、delta 值越小图片会越亮，越大则越暗
	 * 
	 * @param bmp
	 *            要编辑的图片
	 * @return 编辑后的图片
	 */
	public static Bitmap blurImageAmeliorate(Bitmap bmp) {
		if (ImageCache.get("blurImageAmeliorate") != null) {
			return ImageCache.get("blurImageAmeliorate");
		}
		long start = System.currentTimeMillis();
		// 高斯矩阵
		int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int delta = 15; // 值越小图片会越亮，越大则越暗

		int idx = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * gauss[idx]);
						newG = newG + (int) (pixG * gauss[idx]);
						newB = newB + (int) (pixB * gauss[idx]);
						idx++;
					}
				}

				newR /= delta;
				newG /= delta;
				newB /= delta;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("may", "used time=" + (end - start));
		ImageCache.put("blurImageAmeliorate", bitmap);
		return bitmap;
	}

	/**
	 * 底片效果
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap negativeImageAmeliorate(Bitmap bmp) {
		if (ImageCache.get("negativeImageAmeliorate") != null) {
			return ImageCache.get("negativeImageAmeliorate");
		}
		// RGBA的最大值
		final int MAX_VALUE = 255;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = MAX_VALUE - pixR;
				newG = MAX_VALUE - pixG;
				newB = MAX_VALUE - pixB;

				newR = Math.min(MAX_VALUE, Math.max(0, newR));
				newG = Math.min(MAX_VALUE, Math.max(0, newG));
				newB = Math.min(MAX_VALUE, Math.max(0, newB));

				pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		ImageCache.put("negativeImageAmeliorate", bitmap);
		return bitmap;
	}

	/**
	 * 图片锐化（拉普拉斯变换）
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap sharpenImageAmeliorate(Bitmap bmp) {
		if (ImageCache.get("sharpenImageAmeliorate") != null) {
			return ImageCache.get("sharpenImageAmeliorate");
		}
		long start = System.currentTimeMillis();
		// 拉普拉斯矩阵
		int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int idx = 0;
		float alpha = 0.3F;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = pixels[(i + n) * width + k + m];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * laplacian[idx] * alpha);
						newG = newG + (int) (pixG * laplacian[idx] * alpha);
						newB = newB + (int) (pixB * laplacian[idx] * alpha);
						idx++;
					}
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("may", "used time=" + (end - start));
		ImageCache.put("sharpenImageAmeliorate", bitmap);
		return bitmap;
	}

	/**
	 * 光照效果
	 * 
	 * @param bmp
	 *            光照中心x坐标
	 * @param centerX
	 *            光照中心要坐标
	 * @param centerY
	 * @return
	 */
	public static Bitmap sunshine(Bitmap bmp, int centerX, int centerY) {

		if (ImageCache.get("sunshine") != null) {
			return ImageCache.get("sunshine");
		}
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;
		int radius = Math.min(centerX, centerY);

		final float strength = 150F; // 光照强度 100~150
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				// 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
				int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(
						centerX - k, 2));
				if (distance < radius * radius) {
					// 按照距离大小计算增加的光照值
					int result = (int) (strength * (1.0 - Math.sqrt(distance)
							/ radius));
					newR = pixR + result;
					newG = pixG + result;
					newB = pixB + result;
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		ImageCache.put("sunshine", bitmap);
		return bitmap;
	}

	/**
	 * 浮雕效果
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap emboss(Bitmap bmp) {
		if (ImageCache.get("emboss") != null) {
			return ImageCache.get("emboss");
		}
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				pixColor = pixels[pos + 1];
				newR = Color.red(pixColor) - pixR + 127;
				newG = Color.green(pixColor) - pixG + 127;
				newB = Color.blue(pixColor) - pixB + 127;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		ImageCache.put("emboss", bitmap);
		return bitmap;
	}

	/**
	 * 将彩色图转换为黑白图
	 * 
	 * @param 位图
	 * @return 返回转换好的位图
	 */
	public static Bitmap blackWhite(Bitmap bmp) {
		if (ImageCache.get("blackWhite") != null) {
			return ImageCache.get("blackWhite");
		}
		int width = bmp.getWidth(); // 获取位图的宽
		int height = bmp.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap newBmp = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
		ImageCache.put("blackWhite", newBmp);
		return newBmp;
	}

	/**
	 * 图片翻转
	 * 
	 * @param bmp
	 * @param flag
	 *            0为水平翻转；1：为垂直翻转
	 * @return
	 */
	public static Bitmap reverseBitmap(Bitmap bmp, int flag) {
		if (ImageCache.get("reverseBitmap") != null) {
			return ImageCache.get("reverseBitmap");
		}
		float[] floats = null;
		switch (flag) {
		case 0: // 水平反转
			floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
			break;
		case 1: // 垂直反转
			floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
			break;
		}

		if (floats != null) {
			Matrix matrix = new Matrix();
			matrix.setValues(floats);
			return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), matrix, true);
		}
		ImageCache.put("reverseBitmap", bmp);
		return bmp;
	}

	
	
	
	
	/**
	 * 素描
	 * 
	 * @param bmp
	 * @return
	 */
 	public static Bitmap sketch(Bitmap bmp) {
		if (ImageCache.get("sketch") != null) {
			return ImageCache.get("sketch");
		}
		long start = System.currentTimeMillis();
		int pos, row, col, clr;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] pixSrc = new int[width * height];
		int[] pixNvt = new int[width * height];
		// 先对图象的像素处理成灰度颜色后再取反
		bmp.getPixels(pixSrc, 0, width, 0, 0, width, height);
		Bitmap mBitmap = Bitmap.createBitmap(width, height, bmp.getConfig());
		for (row = 0; row < height; row++) {
			for (col = 0; col < width; col++) {
				pos = row * width + col;
				pixSrc[pos] = (Color.red(pixSrc[pos])
						+ Color.green(pixSrc[pos]) + Color.blue(pixSrc[pos])) / 3;
				pixNvt[pos] = 255 - pixSrc[pos];
			}
		}

		// 对取反的像素进行高斯模糊, 强度可以设置，暂定为5.0
		gaussGray(pixNvt, 5.0, 5.0, width, height);

		// 灰度颜色和模糊后像素进行差值运算
		for (row = 0; row < height; row++) {
			for (col = 0; col < width; col++) {
				pos = row * width + col;

				clr = pixSrc[pos] << 8;
				clr /= 256 - pixNvt[pos];
				clr = Math.min(clr, 255);

				pixSrc[pos] = Color.rgb(clr, clr, clr);
			}
		}
		mBitmap.setPixels(pixSrc, 0, width, 0, 0, width, height);
		long end = System.currentTimeMillis();
		Log.d("blurImageAmeliorate", "used time=" + (end - start));
		ImageCache.put("sketch", mBitmap);
		return mBitmap;
	}

	private static int gaussGray(int[] psrc, double horz, double vert,
			int width, int height) {
		int[] dst, src;
		double[] n_p, n_m, d_p, d_m, bd_p, bd_m;
		double[] val_p, val_m;
		int i, j, t, k, row, col, terms;
		int[] initial_p, initial_m;
		double std_dev;
		int row_stride = width;
		int max_len = Math.max(width, height);
		int sp_p_idx, sp_m_idx, vp_idx, vm_idx;

		val_p = new double[max_len];
		val_m = new double[max_len];

		n_p = new double[5];
		n_m = new double[5];
		d_p = new double[5];
		d_m = new double[5];
		bd_p = new double[5];
		bd_m = new double[5];

		src = new int[max_len];
		dst = new int[max_len];

		initial_p = new int[4];
		initial_m = new int[4];

		// 垂直方向
		if (vert > 0.0) {
			vert = Math.abs(vert) + 1.0;
			std_dev = Math.sqrt(-(vert * vert) / (2 * Math.log(1.0 / 255.0)));

			// 初试化常量
			findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);

			for (col = 0; col < width; col++) {
				for (k = 0; k < max_len; k++) {
					val_m[k] = val_p[k] = 0;
				}

				for (t = 0; t < height; t++) {
					src[t] = psrc[t * row_stride + col];
				}

				sp_p_idx = 0;
				sp_m_idx = height - 1;
				vp_idx = 0;
				vm_idx = height - 1;

				initial_p[0] = src[0];
				initial_m[0] = src[height - 1];

				for (row = 0; row < height; row++) {
					terms = (row < 4) ? row : 4;

					for (i = 0; i <= terms; i++) {
						val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
								* val_p[vp_idx - i];
						val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
								* val_m[vm_idx + i];
					}
					for (j = i; j <= 4; j++) {
						val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
						val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
					}

					sp_p_idx++;
					sp_m_idx--;
					vp_idx++;
					vm_idx--;
				}

				transferGaussPixels(val_p, val_m, dst, 1, height);

				for (t = 0; t < height; t++) {
					psrc[t * row_stride + col] = dst[t];
				}
			}
		}

		// 水平方向
		if (horz > 0.0) {
			horz = Math.abs(horz) + 1.0;

			if (horz != vert) {
				std_dev = Math.sqrt(-(horz * horz)
						/ (2 * Math.log(1.0 / 255.0)));

				// 初试化常量
				findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);
			}

			for (row = 0; row < height; row++) {
				for (k = 0; k < max_len; k++) {
					val_m[k] = val_p[k] = 0;
				}

				for (t = 0; t < width; t++) {
					src[t] = psrc[row * row_stride + t];
				}

				sp_p_idx = 0;
				sp_m_idx = width - 1;
				vp_idx = 0;
				vm_idx = width - 1;

				initial_p[0] = src[0];
				initial_m[0] = src[width - 1];

				for (col = 0; col < width; col++) {
					terms = (col < 4) ? col : 4;

					for (i = 0; i <= terms; i++) {
						val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
								* val_p[vp_idx - i];
						val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
								* val_m[vm_idx + i];
					}
					for (j = i; j <= 4; j++) {
						val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
						val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
					}

					sp_p_idx++;
					sp_m_idx--;
					vp_idx++;
					vm_idx--;
				}

				transferGaussPixels(val_p, val_m, dst, 1, width);

				for (t = 0; t < width; t++) {
					psrc[row * row_stride + t] = dst[t];
				}
			}
		}

		return 0;
	}

	private static void findConstants(double[] n_p, double[] n_m, double[] d_p,
			double[] d_m, double[] bd_p, double[] bd_m, double std_dev) {
		double div = Math.sqrt(2 * 3.141593) * std_dev;
		double x0 = -1.783 / std_dev;
		double x1 = -1.723 / std_dev;
		double x2 = 0.6318 / std_dev;
		double x3 = 1.997 / std_dev;
		double x4 = 1.6803 / div;
		double x5 = 3.735 / div;
		double x6 = -0.6803 / div;
		double x7 = -0.2598 / div;
		int i;

		n_p[0] = x4 + x6;
		n_p[1] = (Math.exp(x1)
				* (x7 * Math.sin(x3) - (x6 + 2 * x4) * Math.cos(x3)) + Math
				.exp(x0) * (x5 * Math.sin(x2) - (2 * x6 + x4) * Math.cos(x2)));
		n_p[2] = (2
				* Math.exp(x0 + x1)
				* ((x4 + x6) * Math.cos(x3) * Math.cos(x2) - x5 * Math.cos(x3)
						* Math.sin(x2) - x7 * Math.cos(x2) * Math.sin(x3)) + x6
				* Math.exp(2 * x0) + x4 * Math.exp(2 * x1));
		n_p[3] = (Math.exp(x1 + 2 * x0)
				* (x7 * Math.sin(x3) - x6 * Math.cos(x3)) + Math.exp(x0 + 2
				* x1)
				* (x5 * Math.sin(x2) - x4 * Math.cos(x2)));
		n_p[4] = 0.0;

		d_p[0] = 0.0;
		d_p[1] = -2 * Math.exp(x1) * Math.cos(x3) - 2 * Math.exp(x0)
				* Math.cos(x2);
		d_p[2] = 4 * Math.cos(x3) * Math.cos(x2) * Math.exp(x0 + x1)
				+ Math.exp(2 * x1) + Math.exp(2 * x0);
		d_p[3] = -2 * Math.cos(x2) * Math.exp(x0 + 2 * x1) - 2 * Math.cos(x3)
				* Math.exp(x1 + 2 * x0);
		d_p[4] = Math.exp(2 * x0 + 2 * x1);

		for (i = 0; i <= 4; i++) {
			d_m[i] = d_p[i];
		}

		n_m[0] = 0.0;
		for (i = 1; i <= 4; i++) {
			n_m[i] = n_p[i] - d_p[i] * n_p[0];
		}

		double sum_n_p, sum_n_m, sum_d;
		double a, b;

		sum_n_p = 0.0;
		sum_n_m = 0.0;
		sum_d = 0.0;

		for (i = 0; i <= 4; i++) {
			sum_n_p += n_p[i];
			sum_n_m += n_m[i];
			sum_d += d_p[i];
		}

		a = sum_n_p / (1.0 + sum_d);
		b = sum_n_m / (1.0 + sum_d);

		for (i = 0; i <= 4; i++) {
			bd_p[i] = d_p[i] * a;
			bd_m[i] = d_m[i] * b;
		}
	}

	private static void transferGaussPixels(double[] src1, double[] src2,
			int[] dest, int bytes, int width) {
		int i, j, k, b;
		int bend = bytes * width;
		double sum;

		i = j = k = 0;
		for (b = 0; b < bend; b++) {
			sum = src1[i++] + src2[j++];

			if (sum > 255)
				sum = 255;
			else if (sum < 0)
				sum = 0;

			dest[k++] = (int) sum;
		}
	}

	/**
	 * 柔化美白(效果不明显)
	 **/
	public static Bitmap SoftGlowFilter(Bitmap bmp) {
		if (ImageCache.get("SoftGlowFilter") != null) {
			return ImageCache.get("SoftGlowFilter");
		}
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int old_r, old_g, old_b, r, g, b;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		// 不能对原始图片进行修改，所以要生成一张图片进行编辑。
		Bitmap mBitmap = Bitmap.createBitmap(width, height, bmp.getConfig());

		int pos = 0;
		int pixColor = 0;
		for (int x = 0; x < (width - 1); x++) {
			for (int y = 0; y < (height - 1); y++) {
				pos = y * width + x;
				pixColor = pixels[pos];

				old_r = Color.red(pixColor);
				old_g = Color.green(pixColor);
				old_b = Color.blue(pixColor);

				pixColor = pixels[pos];

				r = 255 - (255 - old_r) * (255 - Color.red(pixColor)) / 255;
				g = 255 - (255 - old_g) * (255 - Color.green(pixColor)) / 255;
				b = 255 - (255 - old_b) * (255 - Color.blue(pixColor)) / 255;
				// pixels[pos] = Color.argb(255, r, g, b);
				int rgbcolor = (255 << 24) + (r << 16) + (g << 8) + b;
				pixels[pos] = rgbcolor;
			}
		}
		mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		ImageCache.put("SoftGlowFilter", mBitmap);
		return mBitmap;
	}

	/**
	 * 柔化美白--调用方法
	 * 
	 * @param bmp
	 * @param nSigma
	 * @param nBrightness
	 * @param nContrast
	 * @return
	 */
	public static Bitmap SoftGlowFilter(Bitmap bmp, int nSigma,
			float nBrightness, float nContrast) {
		// 速度测试
		long start = System.currentTimeMillis();
		if (ImageCache.get("SoftGlowFilter") != null) {
			return ImageCache.get("SoftGlowFilter");
		}
		// 柔化处理
		GaussianBlurFilter gaussianBlurFx = new GaussianBlurFilter(bmp);
		gaussianBlurFx.Sigma = nSigma;
		ImageData image = gaussianBlurFx.imageProcess();
		// 高亮对比度特效
		BrightContrastFilter contrastFx = new BrightContrastFilter(image);
		contrastFx.BrightnessFactor = nBrightness;
		contrastFx.ContrastFactor = nContrast;

		image = contrastFx.imageProcess(); // 高亮对比度
		Bitmap mBitmap = imageProcess(image).getDstBitmap();
		ImageCache.put("SoftGlowFilter", mBitmap);
		long end = System.currentTimeMillis();
		Log.d("may", "used time=" + (end - start));
		return mBitmap;
	}

	/**
	 * 效果明显的柔化美白
	 * 
	 * @param image
	 * @return
	 */
	private static ImageData imageProcess(ImageData image) {
		int width = image.getWidth();
		int height = image.getHeight();
		ImageData clone = image.clone();
		int old_r, old_g, old_b, r, g, b;
		for (int x = 0; x < (width - 1); x++) {
			for (int y = 0; y < (height - 1); y++) {
				old_r = clone.getRComponent(x, y);
				old_g = clone.getGComponent(x, y);
				old_b = clone.getBComponent(x, y);

				r = 255 - (255 - old_r) * (255 - image.getRComponent(x, y))
						/ 255;
				g = 255 - (255 - old_g) * (255 - image.getGComponent(x, y))
						/ 255;
				b = 255 - (255 - old_b) * (255 - image.getBComponent(x, y))
						/ 255;
				image.setPixelColor(x, y, r, g, b);
			}
		}
		return image;
	}
}
