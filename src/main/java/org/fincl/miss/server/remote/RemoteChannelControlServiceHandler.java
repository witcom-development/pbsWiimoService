package org.fincl.miss.server.remote;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.fincl.miss.core.remote.server.ChannelControl;
import org.fincl.miss.core.remote.server.ChannelControl.Result;
import org.fincl.miss.core.remote.server.RemoteChannelControlService;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.exeption.ChannelControlException;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class RemoteChannelControlServiceHandler implements RemoteChannelControlService {
    
    @Autowired
    private ChannelManagerImpl channelManager;
    
    @Autowired
    private MessageSource errorSource;
    
    @Override
    public ChannelControl startInBoundChannel(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        InBoundChannelImpl inBoundChannel = channelManager.getInBoundChannel(channelId);
        if (inBoundChannel == null) {
            boolean bLoad = false;
            try {
                bLoad = channelManager.loadInBoundChannel(channelId);
                if (bLoad) {
                    channelControl.setResult(Result.SUCCESS);
                }
                else {
                    channelControl.setResult(Result.FAIL);
                    channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_START_ERROR, new String[] { channelId }, Locale.getDefault()));
                }
            }
            catch (ChannelControlException e) {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
            }
        }
        else {
            ChannelStatus channelStatus = inBoundChannel.getStatus();
            if (channelStatus == ChannelStatus.ALIVE) {
                channelControl.setResult(Result.FAIL);
                // 이미 실행중
                channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_START_ERROR, new String[] { channelId }, Locale.getDefault()));
            }
            else {
                boolean bStart = inBoundChannel.startup();
                if (bStart) {
                    channelControl.setResult(Result.SUCCESS);
                }
                else {
                    channelControl.setResult(Result.FAIL);
                    channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_START_ERROR, new String[] { channelId }, Locale.getDefault()));
                }
            }
        }
        return channelControl;
    }
    
    @Override
    public ChannelControl stopInBoundChannel(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        InBoundChannelImpl inBoundChannel = channelManager.getInBoundChannel(channelId);
        if (inBoundChannel == null) {
            boolean bLoad = false;
            try {
                bLoad = channelManager.loadInBoundChannel(channelId);
                if (bLoad) {
                    channelControl.setResult(Result.SUCCESS);
                }
                else {
                    channelControl.setResult(Result.FAIL);
                    channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_STOP_ERROR, new String[] { channelId }, Locale.getDefault()));
                }
            }
            catch (ChannelControlException e) {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
            }
        }
        else {
            ChannelStatus channelStatus = inBoundChannel.getStatus();
            if (channelStatus == ChannelStatus.ALIVE) {
                boolean bShutdown = inBoundChannel.shutdown();
                if (bShutdown) {
                    channelControl.setResult(Result.SUCCESS);
                }
                else {
                    channelControl.setResult(Result.FAIL);
                    channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_STOP_ERROR, new String[] { channelId }, Locale.getDefault()));
                }
            }
            else {
                channelControl.setResult(Result.FAIL);
                // 현재 정지중
                channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_STOP_ERROR, new String[] { channelId }, Locale.getDefault()));
            }
        }
        return channelControl;
    }
    
    @Override
    public ChannelControl getInBoundChannelStatus(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        InBoundChannelImpl inBoundChannel = channelManager.getInBoundChannel(channelId);
        if (inBoundChannel == null) {
            boolean bLoad = false;
            try {
                bLoad = channelManager.loadInBoundChannel(channelId);
                if (bLoad) {
                    inBoundChannel = channelManager.getInBoundChannel(channelId);
                    ChannelStatus channelStatus = inBoundChannel.getStatus();
                    if (channelStatus == ChannelStatus.ALIVE) {
                        channelControl.setResult(Result.ALIVE);
                    }
                    else {
                        channelControl.setResult(Result.DEAD);
                    }
                }
                else {
                    channelControl.setResult(Result.FAIL);
                    channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR, new String[] { channelId }, Locale.getDefault()));
                }
            }
            catch (ChannelControlException e) {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
            }
        }
        else {
            ChannelStatus channelStatus = inBoundChannel.getStatus();
            if (channelStatus == ChannelStatus.ALIVE) {
                channelControl.setResult(Result.ALIVE);
            }
            else {
                channelControl.setResult(Result.DEAD);
            }
        }
        return channelControl;
    }
    
    @Override
    public Map<String, ChannelControl> getInBoundChannelStatus(String[] channelIds) {
        Map<String, ChannelControl> mInBoundChannelStatus = new HashMap<String, ChannelControl>();
        for (String channelId : channelIds) {
            mInBoundChannelStatus.put(channelId, getInBoundChannelStatus(channelId));
        }
        return mInBoundChannelStatus;
    }
    
    @Override
    public ChannelControl startOutBoundChannel(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        OutBoundChannelImpl outBoundChannel = channelManager.getOutBoundChannel(channelId);
        if (outBoundChannel == null) {
            boolean bLoad = false;
            try {
                bLoad = channelManager.loadOutBoundChannel(channelId);
                if (bLoad) {
                    channelControl.setResult(Result.SUCCESS);
                }
                else {
                    channelControl.setResult(Result.FAIL);
                    channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_START_ERROR, new String[] { channelId }, Locale.getDefault()));
                }
            }
            catch (ChannelControlException e) {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
            }
        }
        else {
            ChannelStatus channelStatus = outBoundChannel.getStatus();
            System.out.println(outBoundChannel);
            System.out.println("channelStatus:" + channelStatus);
            if (channelStatus == ChannelStatus.ALIVE) {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_START_ERROR, new String[] { channelId }, Locale.getDefault()));
            }
            else {
                boolean bStart = outBoundChannel.startup();
                if (bStart) {
                    channelControl.setResult(Result.SUCCESS);
                }
                else {
                    channelControl.setResult(Result.FAIL);
                    channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_START_ERROR, new String[] { channelId }, Locale.getDefault()));
                }
            }
        }
        return channelControl;
    }
    
    @Override
    public ChannelControl stopOutBoundChannel(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        OutBoundChannelImpl outBoundChannel = channelManager.getOutBoundChannel(channelId);
        System.out.println("outBoundChannel:" + outBoundChannel);
        // OutBoundChannel은 stop시 remove
        if (outBoundChannel == null) {
            channelControl.setResult(Result.FAIL);
            channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_ALREADY_STOPPED, new String[] { channelId }, Locale.getDefault()));
        }
        else {
            boolean bRemove = false;
            
            try {
                bRemove = channelManager.removeOutBoundChannel(channelId);
            }
            catch (ServerException e) {
                bRemove = false;
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
            }
            
            if (bRemove) {
                channelControl.setResult(Result.SUCCESS);
            }
            else {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONROL_ERROR, new String[] { channelId }, Locale.getDefault()));
            }
        }
        // if (outBoundChannel == null) {
        // boolean bLoad = false;
        // try {
        // bLoad = channelManager.loadOutBoundChannel(channelId);
        // if (bLoad) {
        // channelControlVO.setResult(Result.SUCCESS);
        // }
        // else {
        // channelControlVO.setResult(Result.FAIL);
        // channelControlVO.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_STOP_ERROR, new String[] { channelId }, Locale.getDefault()));
        // }
        // }
        // catch (ChannelControlException e) {
        // channelControlVO.setResult(Result.FAIL);
        // channelControlVO.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
        // }
        // }
        // else {
        // ChannelStatus channelStatus = outBoundChannel.getStatus();
        // System.out.println("channelStatus:" + channelStatus);
        // if (channelStatus == ChannelStatus.ALIVE) {
        // boolean bShutdown = outBoundChannel.shutdown();
        // if (bShutdown) {
        // channelControlVO.setResult(Result.SUCCESS);
        // }
        // else {
        // channelControlVO.setResult(Result.FAIL);
        // channelControlVO.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_STOP_ERROR, new String[] { channelId }, Locale.getDefault()));
        // }
        // }
        // else {
        // channelControlVO.setResult(Result.FAIL);
        // channelControlVO.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_STOP_ERROR, new String[] { channelId }, Locale.getDefault()));
        // }
        // }
        return channelControl;
    }
    
    @Override
    public ChannelControl getOutBoundChannelStatus(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        OutBoundChannelImpl outBoundChannel = channelManager.getOutBoundChannel(channelId);
        if (outBoundChannel == null) {
            // boolean bLoad = false;
            channelControl.setResult(Result.DEAD);
            // try {
            // bLoad = channelManager.loadOutBoundChannel(channelId);
            // if (bLoad) {
            // outBoundChannel = channelManager.getOutBoundChannel(channelId);
            // ChannelStatus channelStatus = outBoundChannel.getStatus();
            // if (channelStatus == ChannelStatus.ALIVE) {
            // channelControlVO.setResult(Result.ALIVE);
            // }
            // else {
            // channelControlVO.setResult(Result.DEAD);
            // }
            // }
            // else {
            // channelControlVO.setResult(Result.FAIL);
            // channelControlVO.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR, new String[] { channelId }, Locale.getDefault()));
            // }
            // }
            // catch (ChannelControlException e) {
            // channelControlVO.setResult(Result.FAIL);
            // channelControlVO.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
            // }
        }
        else {
            ChannelStatus channelStatus = outBoundChannel.getStatus();
            if (channelStatus == ChannelStatus.ALIVE) {
                channelControl.setResult(Result.ALIVE);
            }
            else {
                channelControl.setResult(Result.DEAD);
            }
        }
        return channelControl;
    }
    
    @Override
    public Map<String, ChannelControl> getOutBoundChannelStatus(String[] channelIds) {
        Map<String, ChannelControl> mOutBoundChannelStatus = new HashMap<String, ChannelControl>();
        for (String channelId : channelIds) {
            mOutBoundChannelStatus.put(channelId, getOutBoundChannelStatus(channelId));
        }
        return mOutBoundChannelStatus;
    }
    
    @Override
    public ChannelControl reloadInBoundChannel(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        InBoundChannelImpl inBoundChannel = channelManager.getInBoundChannel(channelId);
        if (inBoundChannel == null) {
            channelControl.setResult(Result.FAIL);
            channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR, new String[] { channelId }, Locale.getDefault()));
        }
        else {
            boolean bResult = false;
            try {
                bResult = channelManager.unloadInBoundchannel(channelId);
                if (bResult) {
                    bResult = channelManager.loadInBoundChannel(channelId);
                }
                if (bResult) {
                    channelControl.setResult(Result.SUCCESS);
                }
                else {
                    channelControl.setResult(Result.FAIL);
                    channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_RELOAD_ERROR, new String[] { channelId }, Locale.getDefault()));
                }
            }
            catch (ChannelControlException e) {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
            }
        }
        return channelControl;
    }
    
    @Override
    public ChannelControl reloadOutBoundChannel(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        OutBoundChannelImpl outBoundChannel = channelManager.getOutBoundChannel(channelId);
        if (getOutBoundChannelStatus(channelId).getResult() == ChannelControl.Result.ALIVE) {
            channelControl = stopOutBoundChannel(channelId);
            channelControl = startOutBoundChannel(channelId);
        }
        else {
            channelControl.setResult(Result.SUCCESS);
        }
        // if (outBoundChannel == null) {
        // channelControlVO.setResult(Result.FAIL);
        // channelControlVO.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR, new String[] { channelId }, Locale.getDefault()));
        // }
        // else {
        // boolean bResult = false;
        //
        // try {
        // bResult = channelManager.unloadOutBoundchannel(channelId);
        // if (bResult) {
        // bResult = channelManager.loadOutBoundChannel(channelId);
        // }
        //
        // if (bResult) {
        // channelControlVO.setResult(Result.SUCCESS);
        // }
        // else {
        // channelControlVO.setResult(Result.FAIL);
        // channelControlVO.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_RELOAD_ERROR, new String[] { channelId }, Locale.getDefault()));
        // }
        // }
        // catch (ChannelControlException e) {
        // channelControlVO.setResult(Result.FAIL);
        // channelControlVO.setMessage(errorSource.getMessage(e.getCode(), new String[] { channelId }, Locale.getDefault()));
        // }
        //
        // }
        return channelControl;
    }
    
    @Override
    public ChannelControl removeInBoundchannel(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        InBoundChannelImpl inBoundChannel = channelManager.getInBoundChannel(channelId);
        if (inBoundChannel == null) {
            channelControl.setResult(Result.FAIL);
            channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR, new String[] { channelId }, Locale.getDefault()));
        }
        else {
            boolean bResult = channelManager.removeInBoundChannel(channelId);
            if (bResult) {
                channelControl.setResult(Result.SUCCESS);
            }
            else {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_REMOVE_ERROR, new String[] { channelId }, Locale.getDefault()));
            }
        }
        return channelControl;
    }
    
    @Override
    public ChannelControl removeOutBoundChannel(String channelId) {
        ChannelControl channelControl = new ChannelControl();
        OutBoundChannelImpl outBoundChannel = channelManager.getOutBoundChannel(channelId);
        if (outBoundChannel == null) {
            channelControl.setResult(Result.FAIL);
            channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR, new String[] { channelId }, Locale.getDefault()));
        }
        else {
            boolean bResult = channelManager.removeOutBoundChannel(channelId);
            if (bResult) {
                channelControl.setResult(Result.SUCCESS);
            }
            else {
                channelControl.setResult(Result.FAIL);
                channelControl.setMessage(errorSource.getMessage(ErrorConstant.CHANNEL_CONTROL_REMOVE_ERROR, new String[] { channelId }, Locale.getDefault()));
            }
        }
        return channelControl;
    }
    
}
