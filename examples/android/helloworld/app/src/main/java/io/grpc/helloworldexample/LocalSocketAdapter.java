/*
 */
package io.grpc.helloworldexample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImplFactory;
import java.net.SocketOptions;
import java.nio.channels.SocketChannel;
import java.util.Vector;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

/**
 * Adaptor allows using a LocalSocket as a Socket.
 */
final class LocalSocketAdapter extends Socket {
    private final LocalSocketAddress address;
    private final LocalSocket unix;
    private final SocketAddress localAddress;
    private InetSocketAddress inetSocketAddress;
    private InputStream is;
    private OutputStream os;

    LocalSocketAdapter(LocalSocketAddress address) {
	this.address = address;
	this.localAddress = new InetSocketAddress(0);
	unix = new LocalSocket();
    }

    LocalSocketAdapter(LocalSocketAddress address, InetSocketAddress inetAddress) {
	this(address);
	this.inetSocketAddress = inetAddress;
    }

    private void throwUnsupportedOperationException() {
	Log.i("helloworld", "Unsupported: " + Log.getStackTraceString(new Exception()));
	throw new UnsupportedOperationException();
    }

    @Override
    public void bind (SocketAddress bindpoint) {
	throwUnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
	unix.close();
    }

    @Override public void connect(SocketAddress endpoint) throws IOException {
	this.inetSocketAddress = (InetSocketAddress) endpoint;
	try {
	    unix.connect(address);
	} catch (IOException e) {
	    Log.i("helloworld", "Error: " + e.toString());
	    throw e;
	}
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
	this.inetSocketAddress = (InetSocketAddress) endpoint;
	unix.connect(address, timeout);
    }

    @Override
    public SocketChannel getChannel() {
	throwUnsupportedOperationException();
	return null;
    }

    @Override public InetAddress getInetAddress() {
	return inetSocketAddress.getAddress();
    }

    @Override
    public InputStream getInputStream() throws IOException {
	is = unix.getInputStream();
	return is;
    }

    @Override
    public boolean getKeepAlive() {
	throwUnsupportedOperationException();
	return false;
    }

    @Override
    public InetAddress getLocalAddress() {
	throwUnsupportedOperationException();
	return null;
    }

    @Override
    public int getLocalPort() {
	throwUnsupportedOperationException();
	return 0;
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
	//throwUnsupportedOperationException();
	return localAddress;
    }

    @Override
    public boolean getOOBInline() {
	throwUnsupportedOperationException();
	return false;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
	if (os != null)
	    return os;

	OutputStream unixOs = unix.getOutputStream();
	os = new OutputStream() {
		@Override
		public void close() throws IOException {
		    // LocalSocket's default implementation closes the socket,
		    // which leaves readers of thes InputStream hanging.
		    // Instead shutdown input (and output) to release readers.
		    LocalSocketAdapter.this.shutdownInput();
		    LocalSocketAdapter.this.shutdownOutput();
		}

		@Override
		public void write (byte[] b) throws IOException {
		    unixOs.write(b);
		}

		@Override
		public void write (byte[] b, int off, int len) throws IOException {
		    unixOs.write(b, off, len);
		}

		@Override
		public void write (int b) throws IOException {
		    unixOs.write(b);
		}
	    };
	return os;
    }

    @Override
    public int getPort() {
	return inetSocketAddress.getPort();
    }

    @Override
    public int getReceiveBufferSize() throws SocketException {
	try {
	    return unix.getReceiveBufferSize();
	} catch (IOException e) {
	    throw new SocketException(e.getMessage());
	}
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
	return inetSocketAddress;
    }

    @Override
    public boolean getReuseAddress() {
	throwUnsupportedOperationException();
	return false;
    }

    @Override
    public int getSendBufferSize() throws SocketException {
	try {
	    return unix.getSendBufferSize();
	} catch (IOException e) {
	    throw new SocketException(e.getMessage());
	}
    }

    @Override
    public int getSoLinger() {
	throwUnsupportedOperationException();
	return 0;
    }

    @Override
    public int getSoTimeout() throws SocketException {
	try {
	    return unix.getSoTimeout();
	} catch (IOException e) {
	    throw new SocketException(e.getMessage());
	}
    }

    @Override
    public boolean getTcpNoDelay() {
	throwUnsupportedOperationException();
	return false;
    }

    @Override
    public int getTrafficClass() {
	throwUnsupportedOperationException();
	return 0;
    }

    @Override
    public boolean isBound() {
	return unix.isBound();
    }

    @Override
    public boolean isClosed() {
	return unix.isClosed();
    }

    @Override
    public boolean isConnected() {
	return unix.isConnected();
    }

    @Override
    public boolean isInputShutdown() {
	return unix.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown() {
	return unix.isOutputShutdown();
    }

    @Override
    public void sendUrgentData (int data) {
	throwUnsupportedOperationException();
    }

    @Override
    public void setKeepAlive (boolean on) {
	throwUnsupportedOperationException();
    }

    @Override
    public void setOOBInline (boolean on) {
	throwUnsupportedOperationException();
    }

    @Override
    public void setPerformancePreferences (int connectionTime,
					   int latency,
					   int bandwidth) {
		throwUnsupportedOperationException();
    }

    @Override
    public void setReceiveBufferSize(int size) throws SocketException {
	try {
	    unix.setReceiveBufferSize(size);
	} catch (IOException e) {
	    throw new SocketException(e.getMessage());
	}
    }

    @Override
    public void setReuseAddress (boolean on) {
	throwUnsupportedOperationException();
    }

    @Override
    public void setSendBufferSize(int size) throws SocketException {
	try {
	    unix.setSendBufferSize(size);
	} catch (IOException e) {
	    throw new SocketException(e.getMessage());
	}
    }

    @Override
    public void setSoLinger (boolean on,
			     int linger) {
	throwUnsupportedOperationException();
    }

    @Override
    public void setSoTimeout(int timeout) throws SocketException {
	try {
	    unix.setSoTimeout(timeout);
	} catch (IOException e) {
	    throw new SocketException(e.getMessage());
	}
    }

    @Override
    public void setTcpNoDelay (boolean on) {
	// Not relevant for local sockets.
    }

    @Override
    public void setTrafficClass (int tc) {
	throwUnsupportedOperationException();
    }

    @Override
    public void shutdownInput() throws IOException {
	unix.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException {
	unix.shutdownOutput();
    }

    @Override
    public String toString() {
	return unix.toString();
    }
}
