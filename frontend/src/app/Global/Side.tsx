"use client";
import React, { ReactNode, useEffect } from "react";
import ReactDOM from "react-dom";

interface ModalProps {
  open: boolean;
  onClose: () => void;
  children: ReactNode;
  outlineClassName?: string;
  className?: string;
  escClose: boolean;
  outlineClose: boolean;
}

const Side = ({ open, onClose, children, outlineClassName, className, escClose, outlineClose }: ModalProps) => {
  useEffect(() => {
    const onWindowKeydown = (e: any) => {
      if (e.key == 'Escape')
        onClose();
    };
    if (escClose) {
      window.addEventListener('keydown', onWindowKeydown);
      return () => window.removeEventListener('keydown', onWindowKeydown);
    }
  }, [escClose]);
  if (!open) return null;
  const portal = ReactDOM.createPortal(
    <>
      <div className={outlineClassName + ' fixed top-0 left-0 right-0 bottom-0 bg-black bg-opacity-30'} onClick={() => { if (outlineClose) onClose(); }} />
      <div className={className + ' fixed p-0 bg-white top-0 z-[5]'}>
        {children}
      </div>
    </>,
    document.getElementById("global-modal") as HTMLElement
  );
  return portal;
};

export default Side;