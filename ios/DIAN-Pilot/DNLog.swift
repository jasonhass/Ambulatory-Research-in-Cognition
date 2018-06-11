/*
Copyright (c) 2017 Washington University in St. Louis 
Created by: Jason J. Hassenstab, PhD

Washington University in St. Louis hereby grants to you a non-transferable, non-exclusive, royalty-free license to use and copy the computer code provided here (the "Software").  You agree to include this license and the above copyright notice in all copies of the Software.  The Software may not be distributed, shared, or transferred to any third party.  This license does not grant any rights or licenses to any other patents, copyrights, or other forms of intellectual property owned or controlled by Washington University in St. Louis.

YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE, THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 
*/

import UIKit

class DNLogManager: NSObject {

    static public let sharedInstance = DNLogManager();
    
    var logToFile:Bool = false;
    
    var fh: FileHandle?;
    var fm = FileManager.default;
    var logName:String = "DNLog.txt";
    
    static public func setLogToFile(log: Bool)
    {
        DNLogManager.sharedInstance.logToFile = log;
    }
    
    static public func logString(_ s:String)
    {
        DNLogManager.sharedInstance.logString(s);
    }
    
    private func logString(_ s:String)
    {
        if logToFile == false
        {
            return;
        }
        
        let documentsDirecotry = fm.urls(for: .documentDirectory, in: .userDomainMask)[0];
        let filepath = documentsDirecotry.appendingPathComponent(logName);
        let line = "\(Date().localizedString(dateStyle: .short, timeStyle: .medium)): \(s)\n";
        
        do
        {
            if fm.fileExists(atPath: filepath.path) == false
            {
                try line.write(to: filepath, atomically: true, encoding: .utf8);
            }
            else
            {
                if fh == nil
                {
                    self.fh = try FileHandle(forWritingTo: filepath);
                }
                
                if let fileHandle = fh
                {
                    _ = fileHandle.seekToEndOfFile();
                    if let data = line.data(using: String.Encoding.utf8) {
                        fileHandle.write(data)
                    }
                }
            }
        }
        catch
        {
            print("error writing to log: \(error)");
        }
    }
    
}

func DNLog(_ s: String, quiet:Bool = false)
{
    if !quiet
    {
        print(s);
    }
    
    if DNLogManager.sharedInstance.logToFile
    {
        DNLogManager.logString(s);
    }
}
