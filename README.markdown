CWAC Updater: App Updates, No Market Required
=============================================
to use this lib do this steps:

1- update SimpleHttpDownloadStrategy file to : 
     
     this change add a static variable to get download filesize and show it in progressbar.
          
	public static int Filesize;
	final File SDCardRoot = Environment.getExternalStorageDirectory();
	
	public float getStockName2() {
		return this.Filesize;
	}
	public float data_send_to_act() {
		System.out.println("Your File Total size is " + "/" + Filesize);
		return Filesize;
	}

	public static long getFolderSize(File f) {
		long size = 0;
		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				size += getFolderSize(file);
			}
		} else {
			size = f.length();
		}
		return size;
	}

	@Override
	public Uri downloadAPK(Context ctxt, String url) throws Exception {
		File apk = getDownloadFile(ctxt);

		if (apk.exists()) {
			apk.delete();
		}

		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		try {
			conn.connect();

			int status = conn.getResponseCode();

			if (status == 200) {
				InputStream is = conn.getInputStream();
				OutputStream f = openDownloadFile(ctxt, apk);
				byte[] buffer = new byte[4096];
				int len1 = 0;
				// totalSize = conn.getContentLength();

				while ((len1 = is.read(buffer)) > 0) {
					// pb.setProgress((int) files.length());
					f.write(buffer, 0, len1);
					this.Filesize = (int) ((getFolderSize(apk) / 1024) / 100);// call function
					data_send_to_act();
				}

				f.close();
				is.close();

			} else {
				throw new RuntimeException(String.format("Received %d from server", status));
			}
		} finally {
			conn.disconnect();
		}

		return (getDownloadUri(ctxt, apk));
	}

	@Override
	public int describeContents() {
		return (0);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// no-op
	}

	protected File getDownloadFile(Context ctxt) {
		File updateDir = new File(ctxt.getExternalFilesDir(null), "EmabaUpdate");
		updateDir.mkdirs();
		return (new File(updateDir, "Ebama.apk"));
	}

	protected OutputStream openDownloadFile(Context ctxt, File apk) throws FileNotFoundException {
		return (new FileOutputStream(apk));
	}

	protected Uri getDownloadUri(Context ctxt, File apk) {
		return (Uri.fromFile(apk));
	}

	public static final Parcelable.Creator<SimpleHttpDownloadStrategy> CREATOR = new Parcelable.Creator<SimpleHttpDownloadStrategy>() {
		public SimpleHttpDownloadStrategy createFromParcel(Parcel in) {
			return (new SimpleHttpDownloadStrategy());
		}

		public SimpleHttpDownloadStrategy[] newArray(int size) {
			return (new SimpleHttpDownloadStrategy[size]);
		}
	};
     
